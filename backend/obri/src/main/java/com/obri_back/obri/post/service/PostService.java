package com.obri_back.obri.post.service;

import com.obri_back.obri.application.service.ApplicationService;
import com.obri_back.obri.global.exception.ForbiddenException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.notification.NotificationService;
import com.obri_back.obri.post.dto.PostCreateRequestDTO;
import com.obri_back.obri.post.dto.PostDetailResponseDTO;
import com.obri_back.obri.post.dto.PostResponseDTO;
import com.obri_back.obri.post.dto.PostSummaryResponseDTO;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostInstrument;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.post.repository.PostRepository;
import com.obri_back.obri.post.repository.PostSpecification;
import com.obri_back.obri.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    // 단건 조회 시 applicationCount·hasApplied 계산을 위해 Application 도메인 참조
    // (UserController → ApplicationService와 동일한 기존 패턴, CLAUDE.md 개선 백로그 항목)
    private final ApplicationService applicationService;
    private final NotificationService notificationService;

    @Transactional
    public PostResponseDTO createPost(User user, PostCreateRequestDTO request) {
        Post post = Post.create(user, request);
        request.getInstruments().forEach(item ->
                post.addInstrument(PostInstrument.of(post, item.getInstrument(), item.getPeople()))
        );
        Post saved = postRepository.save(post);
        // 새 구인글 → 전체 구독자에게 broadcast (발송 실패는 NotificationService에서 격리)
        notificationService.notifyNewPost(saved.getId(), saved.getTitle());
        return PostResponseDTO.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryResponseDTO> getPosts(List<String> categories, List<String> instruments,
            List<String> regions, LocalDate startDate, LocalDate endDate, PostStatus status, Pageable pageable) {
        Specification<Post> spec = PostSpecification.filter(categories, instruments, regions, startDate, endDate, status);
        return postRepository.findAll(spec, pageable).map(PostSummaryResponseDTO::from);
    }

    // 내가 올린 구인글 목록(마이페이지) — 카드 리스트용 요약. 정렬은 컨트롤러 Pageable에서 결정
    @Transactional(readOnly = true)
    public Page<PostSummaryResponseDTO> getMyPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable).map(PostSummaryResponseDTO::from);
    }

    @Transactional(readOnly = true)
    public PostDetailResponseDTO getPost(Long postId, User user) {
        Post post = findPostOrThrow(postId);

        long applicationCount = applicationService.countApplicationsByPostId(postId);
        boolean isMine = post.getUser().getId().equals(user.getId());
        boolean hasApplied = applicationService.hasApplied(postId, user.getId());

        return PostDetailResponseDTO.from(post, applicationCount, isMine, hasApplied);
    }

    @Transactional
    public PostResponseDTO updatePost(Long postId, User user, PostCreateRequestDTO request) {
        Post post = findPostOrThrow(postId);
        requireOwner(post, user);

        post.updateInfo(request);

        List<PostInstrument> newInstruments = request.getInstruments().stream()
                .map(item -> PostInstrument.of(post, item.getInstrument(), item.getPeople()))
                .collect(Collectors.toList());
        post.replaceInstruments(newInstruments);

        // 구인글 수정 → 대기·수락 지원자에게 알림(거절/취소/철회 제외) — 명세 시나리오 1.8
        List<String> applicantTokens = applicationService.getActiveApplicantFcmTokens(postId);
        notificationService.notifyPostUpdated(applicantTokens, post.getId(), post.getTitle());

        return PostResponseDTO.from(post);
    }

    @Transactional
    public void closePost(Long postId, User user) {
        Post post = findPostOrThrow(postId);
        requireOwner(post, user);
        post.close();
    }

    @Transactional
    public void deletePost(Long postId, User user) {
        Post post = findPostOrThrow(postId);
        requireOwner(post, user);

        applicationService.deleteAllByPostId(postId);
        postRepository.delete(post);
    }

    private Post findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("구인글을 찾을 수 없습니다"));
    }

    private void requireOwner(Post post, User user) {
        if (!post.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("작성자만 처리할 수 있습니다");
        }
    }
}
