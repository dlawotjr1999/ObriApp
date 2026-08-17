package com.obri_back.obri.post.controller;

import com.obri_back.obri.global.config.SecurityConfig;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.post.dto.PostDetailResponseDTO;
import com.obri_back.obri.post.dto.PostInstrumentDTO;
import com.obri_back.obri.post.dto.PostResponseDTO;
import com.obri_back.obri.post.dto.PostSummaryResponseDTO;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.post.service.PostService;
import com.obri_back.obri.user.entity.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean PostService postService;
    @MockitoBean FirebaseAuthFilter firebaseAuthFilter;

    private Authentication auth;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(
                invocation.getArgument(0, ServletRequest.class),
                invocation.getArgument(1, ServletResponse.class)
            );
            return null;
        }).when(firebaseAuthFilter).doFilter(any(), any(), any());

        User mockUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .firebaseUid("test-uid")
                .phoneNumber("010-1234-5678")
                .nickname("tester")
                .instrument("바이올린")
                .school("서울대")
                .isGraduate(false)
                .build();

        auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
    }

    @Test
    void createPost_returns200WithRegisteredPost() throws Exception {
        PostResponseDTO response = PostResponseDTO.builder()
                .id(1L)
                .category("결혼")
                .title("결혼식 바이올린 구인")
                .eventAt(LocalDateTime.of(2024, 5, 1, 14, 0))
                .location("서울 강남구 OO웨딩홀")
                .timetable("리허설 1회 (13:00), 본식 (14:00)")
                .pay(150000)
                .status(PostStatus.OPEN)
                .instruments(List.of(
                        PostInstrumentDTO.builder().instrument("바이올린").people(2).confirmed(0).closed(false).build()
                ))
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();

        when(postService.createPost(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/posts")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "결혼",
                                  "title": "결혼식 바이올린 구인",
                                  "eventAt": "2024-05-01T14:00:00",
                                  "location": "서울 강남구 OO웨딩홀",
                                  "timetable": "리허설 1회 (13:00), 본식 (14:00)",
                                  "pay": 150000,
                                  "instruments": [
                                    { "instrument": "바이올린", "people": 2 }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구인글이 등록되었습니다"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("결혼식 바이올린 구인"))
                .andExpect(jsonPath("$.data.status").value("OPEN"));
    }

    @Test
    void createPost_returns400WhenTitleMissing() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "결혼",
                                  "eventAt": "2024-05-01T14:00:00",
                                  "location": "서울 강남구 OO웨딩홀",
                                  "timetable": "리허설 1회",
                                  "pay": 150000,
                                  "instruments": [{ "instrument": "바이올린", "people": 2 }]
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPosts_returns200WithPagedList() throws Exception {
        PostSummaryResponseDTO summary = PostSummaryResponseDTO.builder()
                .id(1L)
                .title("결혼식 바이올린 구인")
                .category("결혼")
                .eventAt(LocalDateTime.of(2024, 5, 1, 14, 0))
                .location("서울 강남구 OO웨딩홀")
                .instruments(List.of(
                        PostInstrumentDTO.builder().instrument("바이올린").people(2).confirmed(0).closed(false).build()
                ))
                .timetable("리허설 1회 (13:00), 본식 (14:00)")
                .pay(150000)
                .status(PostStatus.OPEN)
                .build();

        when(postService.getPosts(any(), any(), any(), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/posts")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.hasNext").exists())
                .andExpect(jsonPath("$.data.currentPage").exists());
    }

    @Test
    void getMyPosts_returns200WithPagedList() throws Exception {
        PostSummaryResponseDTO summary = PostSummaryResponseDTO.builder()
                .id(1L)
                .title("결혼식 바이올린 구인")
                .category("결혼")
                .eventAt(LocalDateTime.of(2024, 5, 1, 14, 0))
                .location("서울 강남구 OO웨딩홀")
                .instruments(List.of(
                        PostInstrumentDTO.builder().instrument("바이올린").people(2).confirmed(0).closed(false).build()
                ))
                .timetable("리허설 1회")
                .pay(150000)
                .status(PostStatus.OPEN)
                .build();

        when(postService.getMyPosts(anyLong(), any()))
                .thenReturn(new PageImpl<>(List.of(summary), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/posts/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.hasNext").exists())
                .andExpect(jsonPath("$.data.currentPage").exists());
    }

    @Test
    void getPost_returns200WithDetail() throws Exception {
        PostDetailResponseDTO response = PostDetailResponseDTO.builder()
                .id(1L)
                .writer(PostDetailResponseDTO.Writer.builder().nickname("홍길동").instrument("바이올린").build())
                .applicationCount(3L)
                .isMine(false)
                .hasApplied(false)
                .category("결혼")
                .title("결혼식 바이올린 구인")
                .eventAt(LocalDateTime.of(2024, 5, 1, 14, 0))
                .location("서울 강남구 OO웨딩홀")
                .timetable("리허설 1회 (13:00), 본식 (14:00)")
                .pay(150000)
                .status(PostStatus.OPEN)
                .instruments(List.of(
                        PostInstrumentDTO.builder().instrument("바이올린").people(2).confirmed(0).closed(false).build()
                ))
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();

        when(postService.getPost(anyLong(), any())).thenReturn(response);

        mockMvc.perform(get("/api/posts/1")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.applicationCount").value(3))
                .andExpect(jsonPath("$.data.writer.nickname").value("홍길동"));
    }

    @Test
    void updatePost_returns200WithUpdatedPost() throws Exception {
        PostResponseDTO response = PostResponseDTO.builder()
                .id(1L)
                .category("결혼")
                .title("수정된 제목")
                .eventAt(LocalDateTime.of(2024, 5, 1, 15, 0))
                .location("서울 강남구 OO웨딩홀")
                .timetable("리허설 1회 (14:00), 본식 (15:00)")
                .pay(200000)
                .status(PostStatus.OPEN)
                .instruments(List.of(
                        PostInstrumentDTO.builder().instrument("바이올린").people(3).confirmed(0).closed(false).build()
                ))
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();

        when(postService.updatePost(anyLong(), any(), any())).thenReturn(response);

        mockMvc.perform(put("/api/posts/1")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "category": "결혼",
                                  "title": "수정된 제목",
                                  "eventAt": "2024-05-01T15:00:00",
                                  "location": "서울 강남구 OO웨딩홀",
                                  "timetable": "리허설 1회 (14:00), 본식 (15:00)",
                                  "pay": 200000,
                                  "instruments": [
                                    { "instrument": "바이올린", "people": 3 }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.title").value("수정된 제목"));
    }

    @Test
    void closePost_returns200() throws Exception {
        doNothing().when(postService).closePost(anyLong(), any());

        mockMvc.perform(patch("/api/posts/1/close")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구인글이 마감되었습니다"));
    }

    @Test
    void deletePost_returns200() throws Exception {
        doNothing().when(postService).deletePost(anyLong(), any());

        mockMvc.perform(delete("/api/posts/1")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("구인글이 삭제되었습니다"));
    }
}
