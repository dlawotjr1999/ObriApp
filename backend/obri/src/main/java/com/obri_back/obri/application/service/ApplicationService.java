package com.obri_back.obri.application.service;

import com.obri_back.obri.application.dto.AppRequestDTO;
import com.obri_back.obri.application.dto.AppResponseDTO;
import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.entity.ApplicationStatus;
import com.obri_back.obri.application.repository.ApplicationRepository;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.ConflictException;
import com.obri_back.obri.global.exception.ForbiddenException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.notification.NotificationService;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.post.repository.PostRepository;
import com.obri_back.obri.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/*
Application 관련 비즈니스 로직
- 지원서 제출(지원자 관점)
- 지원서 단건 조회(지원자 관점; 탭하여 들어갔을 때 구체적으로 볼 수 있는 정보들)
- 지원 상태 업데이트 (구인자: ACCEPTED/REJECTED, 지원자: CANCELLED)

- 한 게시글에 대한 지원서 목록 조회(PENDING)
*/

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    // 지원서 제출
    @Transactional
    public AppResponseDTO submitApplication(User user, AppRequestDTO requestDto) {
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(() -> new NotFoundException("구인글을 찾을 수 없습니다"));

        // 마감된 구인글 체크
        if (post.getStatus() == PostStatus.CLOSED) {
            throw new BadRequestException("마감된 구인글에는 지원할 수 없습니다");
        }

        // 공연 날짜가 지난 구인글 체크 (인원이 안 찼어도 날짜가 지나면 지원 불가)
        if (post.getEventAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("이미 종료된 공연에는 지원할 수 없습니다");
        }

        // 본인 글 지원 체크
        if (post.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인 구인글에는 지원할 수 없습니다");
        }

        // 중복 지원 체크 (DB UNIQUE 제약의 사전 방어선)
        if (applicationRepository.existsByPostIdAndUserId(post.getId(), user.getId())) {
            throw new ConflictException("이미 지원한 구인글입니다");
        }

        // 전공 악기는 지원 시점의 프로필 값을 스냅샷으로 저장 (수락 시 모집 악기와 매칭에 사용)
        Application application = Application.builder()
            .user(user)
            .post(post)
            .instrument(user.getInstrument())
            .additionalInfo(requestDto.getAdditionalInfo())
            .status(ApplicationStatus.PENDING)
            .build();

        applicationRepository.save(application);

        // 지원 도착 → 구인자(글 작성자)에게 단건 push (발송 실패는 NotificationService에서 격리)
        notificationService.notifyNewApplication(post.getUser().getFcmToken(), post.getId(), post.getTitle());

        return AppResponseDTO.from(application, user);
    }

    // 한 게시글에 대한 지원서 목록 조회
    @Transactional(readOnly = true)
    public Page<AppResponseDTO> getApplicationsByPostId(Long postId, User user, Pageable pageable) {
         Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("구인글을 찾을 수 없습니다"));

        // 구인자만 조회 가능
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("구인자만 지원자 목록을 조회할 수 있습니다");
        }

        return applicationRepository.findByPostId(postId, pageable)
                .map(application -> AppResponseDTO.from(application, application.getUser()));
    }

    // 아이디로 지원서 목록 조회
    @Transactional(readOnly = true)
    public Page<AppResponseDTO> getApplicationsByUserId(Long userId, Pageable pageable) {
        return applicationRepository.findByUserId(userId, pageable)
                .map(application -> AppResponseDTO.from(application, application.getUser()));
    }

    // 지원서 단건 조회
    @Transactional(readOnly = true)
    public AppResponseDTO getApplication(User user, Long applicationId) {
        
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("지원서를 찾을 수 없습니다"));

        // 구인자 또는 지원자 본인만 조회 가능
        boolean isApplicant = application.getUser().getId().equals(user.getId());
        boolean isRecruiter = application.getPost().getUser().getId().equals(user.getId());

        if (!isApplicant && !isRecruiter) {
            throw new ForbiddenException("조회 권한이 없습니다");
        }

        return AppResponseDTO.from(application, application.getUser());
    }

    // ── 상태 전이 (명세 Application §2.4) ────────────────────────────
    // 전이별 행위자·출발 상태가 하나로 고정 → 엔드포인트/메서드 단위로 인가를 명확히 강제
    // 수락/철회 시 해당 악기 확정 인원·마감은 Post 도메인에 위임
    // 지원 결과(수락/거절)만 지원자에게 단건 push. 발송 실패는 NotificationService에서 격리

    // 수락 (구인자, PENDING → ACCEPTED)
    @Transactional
    public void accept(User user, Long id) {
        Application application = findApplicationOrThrow(id);
        requireRecruiter(user, application, "구인자만 수락 또는 거절할 수 있습니다");
        requirePending(application);
        application.getPost().confirmInstrument(application.getInstrument());
        application.updateStatus(ApplicationStatus.ACCEPTED);
        notificationService.notifyResult(application.getUser().getFcmToken(), true);
    }

    // 거절 (구인자, PENDING → REJECTED)
    @Transactional
    public void reject(User user, Long id) {
        Application application = findApplicationOrThrow(id);
        requireRecruiter(user, application, "구인자만 수락 또는 거절할 수 있습니다");
        requirePending(application);
        application.updateStatus(ApplicationStatus.REJECTED);
        notificationService.notifyResult(application.getUser().getFcmToken(), false);
    }

    // 취소 (지원자, PENDING → CANCELLED). 결과 알림 없음
    @Transactional
    public void cancel(User user, Long id) {
        Application application = findApplicationOrThrow(id);
        if (!application.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("지원자만 지원을 취소할 수 있습니다");
        }
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("대기 중인 지원만 취소할 수 있습니다");
        }
        application.updateStatus(ApplicationStatus.CANCELLED);
    }

    // 철회 (구인자, ACCEPTED → REVOKED; 확정 취소·자리 재오픈). 결과 알림 없음
    @Transactional
    public void revoke(User user, Long id) {
        Application application = findApplicationOrThrow(id);
        requireRecruiter(user, application, "구인자만 수락을 철회할 수 있습니다");
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new BadRequestException("수락된 지원만 철회할 수 있습니다");
        }
        application.getPost().revokeInstrument(application.getInstrument());
        application.updateStatus(ApplicationStatus.REVOKED);
    }

    private Application findApplicationOrThrow(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("지원서를 찾을 수 없습니다"));
    }

    private void requireRecruiter(User user, Application application, String message) {
        if (!application.getPost().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException(message);
        }
    }

    private void requirePending(Application application) {
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new BadRequestException("대기 중인 지원서만 처리할 수 있습니다");
        }
    }

    // 구인글 수정 알림 — Post 도메인에서 호출. 대기·수락 지원자에게 알릴지 여부까지 이 도메인이 결정
    // TODO: 수정 알림의 실제 필요성은 추후 재검토 대상(우선순위 낮음, 논의 2026-07-29)
    @Transactional(readOnly = true)
    public void notifyApplicantsOfPostUpdate(Long postId, String title) {
        List<String> tokens = applicationRepository.findApplicantFcmTokens(postId,
                List.of(ApplicationStatus.PENDING, ApplicationStatus.ACCEPTED));
        notificationService.notifyPostUpdated(tokens, postId, title);
    }

    // 구인글 삭제 처리 — Post 도메인에서 호출(Post row 삭제 전 반드시 먼저 호출, FK 순서 보장)
    // ACCEPTED 지원자 토큰 확보 → 지원서 정리 → 삭제 알림까지 전담
    // TODO: 삭제 알림의 실제 필요성은 추후 재검토 대상(우선순위 낮음, 논의 2026-07-29)
    @Transactional
    public void handlePostDeletion(Long postId, String title) {
        List<String> acceptedTokens = applicationRepository.findApplicantFcmTokens(postId, List.of(ApplicationStatus.ACCEPTED));
        applicationRepository.deleteByPostId(postId);
        notificationService.notifyPostDeleted(acceptedTokens, postId, title);
    }

    // 구인글 단건 조회(applicationCount)용 — Post 도메인에서 호출
    @Transactional(readOnly = true)
    public long countApplicationsByPostId(Long postId) {
        return applicationRepository.countByPostId(postId);
    }

    // 구인글 단건 조회(hasApplied)용 — Post 도메인에서 호출
    @Transactional(readOnly = true)
    public boolean hasApplied(Long postId, Long userId) {
        return applicationRepository.existsByPostIdAndUserId(postId, userId);
    }
}
