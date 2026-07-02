package com.obri_back.obri.auth.controller;

import com.obri_back.obri.auth.dto.FCMTokenUpdateRequestDTO;
import com.obri_back.obri.auth.dto.RegisterRequestDTO;
import com.obri_back.obri.auth.dto.RegisterResponseDTO;
import com.obri_back.obri.auth.service.AuthService;
import com.obri_back.obri.global.common.APIResponse;
import com.obri_back.obri.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 * Firebase Authentication과 연동해 회원가입 및 FCM 토큰 관리
 * POST  /api/auth/register   — 회원가입
 * PATCH /api/auth/fcm-token  — FCM 토큰 갱신
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /*
     * 회원가입
     * Firebase 가입 후 MySQL에 유저 정보 저장
     * Authorization 헤더의 Firebase ID Token에서 UID와 이메일 추출
     *
     * @param authorization Firebase ID Token (Bearer {token})
     * @param request       닉네임, 악기, 학교, 경력 등 추가 정보
     * @return 가입 시각(createdAt)만 포함한 응답
     */
    @PostMapping("/register")
    public ResponseEntity<APIResponse<RegisterResponseDTO>> register(
            @RequestHeader("Authorization") String authorization,
            @RequestBody @Valid RegisterRequestDTO request) {

        // "Bearer " 제거 후 토큰만 추출
        String idToken = authorization.substring(7);
        RegisterResponseDTO response = authService.register(idToken, request);

        return ResponseEntity.ok(APIResponse.ok("회원가입이 완료되었습니다", response));
    }

    /*
     * FCM 토큰 갱신
     * 앱 실행 시 최신 FCM 토큰을 서버에 저장
     * 푸시 알림 발송 시 이 토큰을 사용
     *
     * @param user    현재 로그인한 유저 (SecurityContext에서 추출)
     * @param request 새 FCM 토큰
     * @return 성공 메시지
     */
    @PatchMapping("/fcm-token")
    public ResponseEntity<APIResponse<Void>> updateFcmToken(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid FCMTokenUpdateRequestDTO request) {

        authService.updateFcmToken(user.getFirebaseUid(), request);

        return ResponseEntity.ok(APIResponse.ok("FCM 토큰이 갱신되었습니다"));
    }
}