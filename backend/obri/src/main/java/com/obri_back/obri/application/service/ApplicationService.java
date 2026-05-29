package com.obri_back.obri.application.service;

import com.obri_back.obri.application.dto.AppRequestDTO;
import com.obri_back.obri.application.dto.AppResponseDTO;
import com.obri_back.obri.application.dto.AppStatusUpdateDTO;
import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.repository.ApplicationRepository;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.ForbiddenException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.post.repository.PostRepository;
import com.obri_back.obri.user.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

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

    // 지원서 제출
    @Transactional
    public AppResponseDTO submitApplication(User user, AppRequestDTO requestDto) {
        Post post = postRepository.findById(requestDto.getPostId())
            .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다"));

        // 마감된 구인글 체크
        if (post.getStatus() == PostStatus.CLOSED) {
            throw new BadRequestException("마감된 구인글에는 지원할 수 없습니다");
        }

        // 본인 글 지원 체크
        if (post.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("본인 구인글에는 지원할 수 없습니다");
        }

        Application application = Application.builder()
            .user(user)
            .post(post)
            .additionalInfo(requestDto.getAdditionalInfo())
            .status(true)
            .build();

        applicationRepository.save(application);

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

    // 지원서 상태 업데이트 (승인/거절)
    @Transactional
    public void updateApplicationStatus(User user, Long id, AppStatusUpdateDTO statusUpdateDto) {

    }
}
