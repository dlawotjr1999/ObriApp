package com.obri_back.obri.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.obri_back.obri.auth.dto.FCMTokenUpdateRequestDTO;
import com.obri_back.obri.auth.dto.RegisterRequestDTO;
import com.obri_back.obri.global.exception.ConflictException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.user.entity.Career;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 인증 관련 비즈니스 로직 처리
 * Firebase Authentication과 MySQL 유저 정보를 연동
 * 회원가입 시 Firebase UID로 유저를 식별하고 MySQL에 저장
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    /*
     * 회원가입
     * Firebase ID Token에서 UID와 이메일을 추출해 MySQL에 유저 정보 저장
     * 이미 가입된 firebase_uid면 409 Conflict
     * MySQL 저장 실패 시 Firebase 계정 롤백
     *
     * @param idToken Firebase ID Token (Authorization 헤더에서 추출)
     * @param request 회원가입 요청 DTO
     * @return 저장된 유저 정보
     */
    @Transactional
    public UserResponseDTO register(String idToken, RegisterRequestDTO request) {
        FirebaseToken decodedToken;

        try {
            // Firebase 토큰 검증 및 디코딩
            decodedToken = firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new IllegalArgumentException("유효하지 않은 Firebase 토큰입니다");
        }

        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();

        // 이미 가입된 유저인지 확인
        if (userRepository.existsByFirebaseUid(firebaseUid)) {
            throw new ConflictException("이미 가입된 계정입니다");
        }

        // 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다");
        }

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .firebaseUid(firebaseUid)
                .email(email)
                .nickname(request.getNickname())
                .phoneNumber(request.getPhoneNumber())
                .instrument(request.getInstrument())
                .school(request.getSchool())
                .isGraduate(request.getIsGraduate())
                .build();

        try {
            userRepository.save(user);
        } catch (Exception e) {
            // MySQL 저장 실패 시 Firebase 계정 롤백
            try {
                firebaseAuth.deleteUser(firebaseUid);
            } catch (FirebaseAuthException ex) {
                // Firebase 롤백 실패 로그 (운영 환경에서는 알림 필요)
            }
            throw new RuntimeException("회원가입 중 오류가 발생했습니다");
        }

        // 경력 저장
        if (request.getCareers() != null && !request.getCareers().isEmpty()) {
            List<Career> careers = request.getCareers().stream()
                    .map(dto -> Career.builder()
                            .user(user)
                            .organization(dto.getOrganization())
                            .contexts(dto.getContexts())
                            .build())
                    .collect(Collectors.toList());
            careerRepository.saveAll(careers);
        }

        return UserResponseDTO.from(user);
    }

    /*
     * FCM 토큰 갱신
     * 앱 실행 시 클라이언트에서 최신 FCM 토큰을 전송해 DB 업데이트
     *
     * @param firebaseUid 현재 로그인한 유저의 Firebase UID
     * @param request     FCM 토큰 갱신 요청 DTO
     */
    @Transactional
    public void updateFcmToken(String firebaseUid, FCMTokenUpdateRequestDTO request) {
        User user = userRepository.findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));

        user.updateFcmToken(request.getFcmToken());
    }
}