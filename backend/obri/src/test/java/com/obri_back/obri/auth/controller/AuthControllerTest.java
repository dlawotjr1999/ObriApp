package com.obri_back.obri.auth.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.obri_back.obri.auth.service.AuthService;
import com.obri_back.obri.global.config.SecurityConfig;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.auth.dto.RegisterResponseDTO;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean AuthService authService;

    @TestConfiguration
    static class FilterPassThroughConfig {
        @Bean
        FirebaseAuthFilter firebaseAuthFilter() {
            return new FirebaseAuthFilter(
                    Mockito.mock(FirebaseAuth.class),
                    Mockito.mock(UserRepository.class)) {
                @Override
                protected void doFilterInternal(
                        HttpServletRequest request,
                        HttpServletResponse response,
                        FilterChain filterChain)
                        throws ServletException, IOException {
                    filterChain.doFilter(request, response);
                }
            };
        }
    }

    private Authentication auth;
    private User mockUser;

    @BeforeEach
    void setUp() {
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
        RegisterResponseDTO response = RegisterResponseDTO.builder()
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();

        when(authService.register(any(), any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "tester",
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
                .andExpect(jsonPath("$.data.createdAt").value("2024-01-01T00:00:00"))
                .andExpect(jsonPath("$.data.nickname").doesNotExist());
    }

    @Test
    void register_returns400WhenNicknameMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "instrument": "바이올린",
                                  "school": "서울대",
                                  "isGraduate": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns400WhenInstrumentMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "tester",
                                  "school": "서울대",
                                  "isGraduate": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns400WhenSchoolMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "tester",
                                  "instrument": "바이올린",
                                  "isGraduate": false
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returns400WhenIsGraduateMissing() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nickname": "tester",
                                  "instrument": "바이올린",
                                  "school": "서울대"
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

    @Test
    void updatePhoneNumber_returns200() throws Exception {
        mockMvc.perform(patch("/api/auth/phone-number")
                        .with(authentication(auth))
                        .header("Authorization", "Bearer test-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("전화번호가 변경되었습니다"));

        verify(authService).updatePhoneNumber("test-uid", "test-token");
    }
}
