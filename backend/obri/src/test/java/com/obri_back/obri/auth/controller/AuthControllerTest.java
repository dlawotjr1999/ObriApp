package com.obri_back.obri.auth.controller;

import com.obri_back.obri.auth.service.AuthService;
import com.obri_back.obri.global.config.SecurityConfig;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.user.dto.UserResponseDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean AuthService authService;
    @MockBean FirebaseAuthFilter firebaseAuthFilter;

    private Authentication auth;
    private User mockUser;

    @BeforeEach
    void setUp() throws Exception {
        // FirebaseAuthFilter를 통과시키는 Mock 설정
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(
                invocation.getArgument(0, ServletRequest.class),
                invocation.getArgument(1, ServletResponse.class)
            );
            return null;
        }).when(firebaseAuthFilter).doFilter(any(), any(), any());

        mockUser = User.builder()
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
    void register_returns200WithUserInfo() throws Exception {
        UserResponseDTO response = UserResponseDTO.builder()
                .id(1L)
                .nickname("tester")
                .email("test@test.com")
                .phoneNumber("010-1234-5678")
                .instrument("바이올린")
                .school("서울대")
                .isGraduate(false)
                .careers(List.of())
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();

        when(authService.register(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "tester",
                                  "phoneNumber": "010-1234-5678",
                                  "instrument": "바이올린",
                                  "school": "서울대",
                                  "isGraduate": false,
                                  "careers": [
                                    { "organization": "서울시향", "contexts": "2023년 객원 연주" }
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nickname").value("tester"));
    }

    @Test
    void register_returns400WhenNicknameMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "phoneNumber": "010-1234-5678",
                                  "instrument": "바이올린",
                                  "school": "서울대",
                                  "isGraduate": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateFcmToken_returns200() throws Exception {
        mockMvc.perform(patch("/api/auth/fcm-token")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "fcmToken": "test_fcm_token_12345"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("FCM 토큰이 갱신되었습니다"));
    }
}