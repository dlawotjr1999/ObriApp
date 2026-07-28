package com.obri_back.obri.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.obri_back.obri.global.common.APIResponse;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/*
 * Spring Security 설정 클래스
 * 세션 없는(STATELESS) 토큰 기반 인증 구성 — FirebaseAuthFilter를 인증 필터로 등록하고
 * 인증 예외 경로(회원가입·닉네임 체크·Swagger)를 화이트리스트로 개방
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final FirebaseAuthFilter firebaseAuthFilter;

    /*
     * SecurityFilterChain 빈 등록
     * CSRF 비활성화·세션 STATELESS 설정 후 경로별 인가 규칙을 지정하고
     * FirebaseAuthFilter를 UsernamePasswordAuthenticationFilter 앞에 삽입
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                // 인증 불필요
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/users/check/**").permitAll()
                // Swagger 관련 경로 추가
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(exception ->
                exception.authenticationEntryPoint(authenticationEntryPoint())
            )
            .addFilterBefore(firebaseAuthFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * 미인증 요청 처리
     * 기본 Http403ForbiddenEntryPoint(403 + 빈 바디) 대신 명세의 401 + APIResponse 형식으로 응답
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        ObjectMapper objectMapper = new ObjectMapper();
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(APIResponse.error(401, "인증이 필요합니다")));
        };
    }
}