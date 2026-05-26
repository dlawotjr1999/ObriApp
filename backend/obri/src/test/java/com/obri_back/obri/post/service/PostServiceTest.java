package com.obri_back.obri.post.service;

import com.obri_back.obri.post.dto.PostCreateRequestDTO;
import com.obri_back.obri.post.dto.PostResponseDTO;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock private PostRepository postRepository;
    @InjectMocks private PostService postService;

    private User user;
    private PostCreateRequestDTO request;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@test.com")
                .firebaseUid("test-uid")
                .phoneNumber("010-1234-5678")
                .nickname("tester")
                .instrument("바이올린")
                .school("서울대")
                .isGraduate(false)
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

    @Test
    void createPost_savesPostAndReturnsResponseWithStatusOpen() {
        when(postRepository.save(any(Post.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PostResponseDTO result = postService.createPost(user, request);

        assertThat(result.getStatus()).isEqualTo(PostStatus.OPEN);
        assertThat(result.getTitle()).isEqualTo("결혼식 바이올린 구인");
        assertThat(result.getCategory()).isEqualTo("결혼");
        assertThat(result.getInstruments()).hasSize(2);
        assertThat(result.getInstruments().get(0).getInstrument()).isEqualTo("바이올린");
        assertThat(result.getInstruments().get(1).getInstrument()).isEqualTo("첼로");
        verify(postRepository, times(1)).save(any(Post.class));
    }
}
