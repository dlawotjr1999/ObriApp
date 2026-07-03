package com.obri_back.obri.user.controller;

import com.obri_back.obri.global.config.SecurityConfig;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.user.dto.UserPublicProfileDTO;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.service.UserService;
import com.obri_back.obri.application.service.ApplicationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean UserService userService;
    @MockitoBean ApplicationService applicationService;
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
    void getMyInfo_returns200WithUserInfo() throws Exception {
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

        when(userService.getMyInfo(anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/users/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.nickname").value("tester"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"));
    }

    @Test
    void checkNickname_returns200WithResult() throws Exception {
        when(userService.checkNickname("tester")).thenReturn(true);

        mockMvc.perform(get("/api/users/check/tester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.isDuplicated").value(true));
    }

    @Test
    void getUserProfile_returns200WithUserInfo() throws Exception {
        UserPublicProfileDTO response = UserPublicProfileDTO.builder()
                .nickname("other")
                .instrument("첼로")
                .school("연세대")
                .isGraduate(true)
                .careers(List.of())
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();

        when(userService.getUserProfile("other")).thenReturn(response);

        mockMvc.perform(get("/api/users/other")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.nickname").value("other"))
                .andExpect(jsonPath("$.data.email").doesNotExist())
                .andExpect(jsonPath("$.data.phoneNumber").doesNotExist());
    }

    @Test
    void deleteUser_returns200() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/api/users/me")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200));
    }
}