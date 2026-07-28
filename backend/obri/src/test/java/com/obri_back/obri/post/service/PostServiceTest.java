package com.obri_back.obri.post.service;

import com.obri_back.obri.application.service.ApplicationService;
import com.obri_back.obri.global.exception.ForbiddenException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.post.dto.PostCreateRequestDTO;
import com.obri_back.obri.post.dto.PostDetailResponseDTO;
import com.obri_back.obri.post.dto.PostResponseDTO;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostInstrument;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.post.repository.PostRepository;
import com.obri_back.obri.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @Mock private ApplicationService applicationService;
    @Mock private com.obri_back.obri.notification.NotificationService notificationService;
    @InjectMocks private PostService postService;

    private User owner;
    private User other;
    private PostCreateRequestDTO request;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .id(1L)
                .email("test@test.com")
                .firebaseUid("test-uid")
                .phoneNumber("010-1234-5678")
                .nickname("tester")
                .instrument("바이올린")
                .school("서울대")
                .isGraduate(false)
                .build();

        other = User.builder()
                .id(2L)
                .email("other@test.com")
                .firebaseUid("other-uid")
                .nickname("other")
                .build();

        request = PostCreateRequestDTO.builder()
                .category("결혼")
                .title("결혼식 바이올린 구인")
                .eventAt(LocalDateTime.of(2024, 5, 1, 14, 0))
                .location("서울 강남구 OO웨딩홀")
                .timetable("리허설 1회 (13:00), 본식 (14:00)")
                .pay(150000)
                .instruments(List.of(
                        PostCreateRequestDTO.InstrumentItem.builder()
                                .instrument("바이올린").people(2).build(),
                        PostCreateRequestDTO.InstrumentItem.builder()
                                .instrument("첼로").people(1).build()
                ))
                .build();
    }

    private Post buildPost(User user) {
        Post post = Post.create(user, request);
        request.getInstruments().forEach(item ->
                post.addInstrument(PostInstrument.of(post, item.getInstrument(), item.getPeople())));
        return post;
    }

    @Test
    void createPost_savesPostAndReturnsResponseWithStatusOpen() {
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostResponseDTO result = postService.createPost(owner, request);

        assertThat(result.getStatus()).isEqualTo(PostStatus.OPEN);
        assertThat(result.getTitle()).isEqualTo("결혼식 바이올린 구인");
        assertThat(result.getCategory()).isEqualTo("결혼");
        assertThat(result.getInstruments()).hasSize(2);
        assertThat(result.getInstruments().get(0).getInstrument()).isEqualTo("바이올린");
        assertThat(result.getInstruments().get(0).getConfirmed()).isEqualTo(0);
        assertThat(result.getInstruments().get(0).getClosed()).isFalse();
        assertThat(result.getInstruments().get(1).getInstrument()).isEqualTo("첼로");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getPost_returnsDetailWithApplicationCountAndFlags() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(applicationService.countApplicationsByPostId(10L)).willReturn(3L);
        given(applicationService.hasApplied(10L, other.getId())).willReturn(true);

        PostDetailResponseDTO result = postService.getPost(10L, other);

        assertThat(result.getApplicationCount()).isEqualTo(3L);
        assertThat(result.getIsMine()).isFalse();
        assertThat(result.getHasApplied()).isTrue();
        assertThat(result.getWriter().getNickname()).isEqualTo("tester");
    }

    @Test
    void getPost_throwsNotFoundWhenMissing() {
        given(postRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPost(99L, owner))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updatePost_updatesFieldsWhenOwner() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        PostCreateRequestDTO update = PostCreateRequestDTO.builder()
                .category("결혼")
                .title("수정된 제목")
                .eventAt(LocalDateTime.of(2024, 5, 1, 15, 0))
                .location("서울 강남구 OO웨딩홀")
                .timetable("리허설 1회 (14:00), 본식 (15:00)")
                .pay(200000)
                .instruments(List.of(
                        PostCreateRequestDTO.InstrumentItem.builder()
                                .instrument("플루트").people(1).build()
                ))
                .build();

        PostResponseDTO result = postService.updatePost(10L, owner, update);

        assertThat(result.getTitle()).isEqualTo("수정된 제목");
        assertThat(result.getPay()).isEqualTo(200000);
        assertThat(result.getInstruments()).hasSize(1);
        assertThat(result.getInstruments().get(0).getInstrument()).isEqualTo("플루트");
    }

    @Test
    void updatePost_throwsForbiddenWhenNotOwner() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.updatePost(10L, other, request))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void closePost_setsStatusClosedWhenOwner() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        postService.closePost(10L, owner);

        assertThat(post.getStatus()).isEqualTo(PostStatus.CLOSED);
    }

    @Test
    void closePost_throwsForbiddenWhenNotOwner() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.closePost(10L, other))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void deletePost_deletesApplicationsThenPostAndNotifiesAcceptedApplicantsWhenOwner() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(applicationService.getAcceptedApplicantFcmTokens(10L)).willReturn(List.of("accepted-token"));

        postService.deletePost(10L, owner);

        org.mockito.InOrder inOrder = inOrder(applicationService, postRepository, notificationService);
        inOrder.verify(applicationService).getAcceptedApplicantFcmTokens(10L);
        inOrder.verify(applicationService).deleteAllByPostId(10L);
        inOrder.verify(postRepository).delete(post);
        inOrder.verify(notificationService).notifyPostDeleted(List.of("accepted-token"), 10L, post.getTitle());
    }

    @Test
    void deletePost_throwsForbiddenWhenNotOwner() {
        Post post = buildPost(owner);
        given(postRepository.findById(10L)).willReturn(Optional.of(post));

        assertThatThrownBy(() -> postService.deletePost(10L, other))
                .isInstanceOf(ForbiddenException.class);

        verify(applicationService, never()).getAcceptedApplicantFcmTokens(any());
        verify(applicationService, never()).deleteAllByPostId(any());
        verify(postRepository, never()).delete(any(Post.class));
    }

    @Test
    void getPosts_delegatesToRepositoryWithSpecification() {
        Post post = buildPost(owner);
        when(postRepository.findAll(ArgumentMatchers.<Specification<Post>>any(), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 10), 1));

        var result = postService.getPosts(null, null, null, null, null, null, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("결혼식 바이올린 구인");
    }

    @Test
    void getMyPosts_returnsSummariesForUser() {
        Post post = buildPost(owner);
        given(postRepository.findByUserId(1L, PageRequest.of(0, 10)))
                .willReturn(new PageImpl<>(List.of(post), PageRequest.of(0, 10), 1));

        var result = postService.getMyPosts(1L, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("결혼식 바이올린 구인");
    }
}
