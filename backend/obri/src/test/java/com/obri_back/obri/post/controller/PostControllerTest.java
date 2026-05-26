package com.obri_back.obri.post.controller;

import com.obri_back.obri.global.config.SecurityConfig;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.post.dto.PostInstrumentDTO;
import com.obri_back.obri.post.dto.PostResponseDTO;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean PostService postService;
    @MockBean FirebaseAuthFilter firebaseAuthFilter;

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
                        PostInstrumentDTO.builder().instrument("바이올린").people(2).build()
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
    void createPost_returns400WhenTitleBlank() throws Exception {
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
}
