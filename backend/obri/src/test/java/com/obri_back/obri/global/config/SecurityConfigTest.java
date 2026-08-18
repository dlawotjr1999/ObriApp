package com.obri_back.obri.global.config;

import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.user.controller.UserController;
import com.obri_back.obri.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

// BACKLOG.md #36: 네이티브 앱 런타임은 CORS 대상이 아니라, 로컬에서 Expo web(브라우저)으로 붙는 경우에만 필요.
// app.cors.allowed-origins로 화이트리스트에 있는 오리진만 허용되는지 검증(permitAll 라우트로 인증 없이 확인)
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:8081")
class SecurityConfigTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean UserService userService;
    @MockitoBean FirebaseAuthFilter firebaseAuthFilter;

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
    }

    @Test
    void allowsWhitelistedOrigin() throws Exception {
        mockMvc.perform(get("/api/users/check/tester").header("Origin", "http://localhost:8081"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:8081"));
    }

    @Test
    void rejectsNonWhitelistedOrigin() throws Exception {
        mockMvc.perform(get("/api/users/check/tester").header("Origin", "https://evil.example.com"))
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}
