package com.obri_back.obri.application.service;

import com.obri_back.obri.application.dto.AppRequestDTO;
import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.repository.ApplicationRepository;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.ForbiddenException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.post.repository.PostRepository;
import com.obri_back.obri.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock ApplicationRepository applicationRepository;
    @Mock PostRepository postRepository;

    @InjectMocks ApplicationService applicationService;

    private User applicant;
    private User recruiter;
    private Post post;

    @BeforeEach
    void setUp() {
        applicant = User.builder()
                .id(1L).nickname("applicant").firebaseUid("applicant-uid").build();

        recruiter = User.builder()
                .id(2L).nickname("recruiter").firebaseUid("recruiter-uid").build();

        post = mock(Post.class);
    }

    // ── 지원서 제출 ──────────────────────────────────

    @Test
    void submitApplication_savesWhenValid() {
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(post.getId()).willReturn(10L);
        given(post.getStatus()).willReturn(PostStatus.OPEN);
        given(post.getUser()).willReturn(recruiter);
        given(applicationRepository.save(any(Application.class)))
                .willAnswer(inv -> inv.getArgument(0));

        AppRequestDTO request = AppRequestDTO.from(10L, "추가 정보");

        applicationService.submitApplication(applicant, request);

        verify(applicationRepository, times(1)).save(any(Application.class));
    }

    @Test
    void submitApplication_throwsBadRequestWhenClosed() {
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(post.getStatus()).willReturn(PostStatus.CLOSED);

        AppRequestDTO request = AppRequestDTO.from(10L, null);

        assertThatThrownBy(() -> applicationService.submitApplication(applicant, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("마감된 구인글에는 지원할 수 없습니다");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void submitApplication_throwsForbiddenWhenOwnPost() {
        given(postRepository.findById(10L)).willReturn(Optional.of(post));
        given(post.getStatus()).willReturn(PostStatus.OPEN);
        given(post.getUser()).willReturn(recruiter);

        AppRequestDTO request = AppRequestDTO.from(10L, null);

        assertThatThrownBy(() -> applicationService.submitApplication(recruiter, request))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("본인 구인글에는 지원할 수 없습니다");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void submitApplication_throwsNotFoundWhenPostMissing() {
        given(postRepository.findById(99L)).willReturn(Optional.empty());

        AppRequestDTO request = AppRequestDTO.from(99L, null);

        assertThatThrownBy(() -> applicationService.submitApplication(applicant, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("게시글을 찾을 수 없습니다");
    }
}
