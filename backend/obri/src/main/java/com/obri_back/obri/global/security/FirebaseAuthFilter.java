package com.obri_back.obri.global.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class FirebaseAuthFilter extends OncePerRequestFilter {
    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // 토큰 없으면 다음 필터로
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String idToken = header.substring(7);

        try {
            // Firebase 토큰 검증
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String firebaseUid = decodedToken.getUid();

            // firebase_uid로 MySQL 유저 조회
            userRepository.findByFirebaseUid(firebaseUid).ifPresent(user -> {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList()
                    );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });

        } catch (FirebaseAuthException e) {
            // 토큰 검증 실패 → SecurityContext 비운 채로 다음 필터로
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
