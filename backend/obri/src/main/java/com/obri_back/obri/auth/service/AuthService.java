package com.obri_back.obri.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.obri_back.obri.auth.dto.FCMTokenUpdateRequestDTO;
import com.obri_back.obri.auth.dto.RegisterRequestDTO;
import com.obri_back.obri.auth.dto.RegisterResponseDTO;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.ConflictGuard;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.global.exception.RegistrationFailedException;
import com.obri_back.obri.global.exception.UnauthorizedException;
import com.obri_back.obri.user.entity.Career;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/*
 * 인증 관련 비즈니스 로직 처리
 * Firebase Authentication과 MySQL 유저 정보를 연동
 * 회원가입 시 Firebase UID로 유저를 식별하고 MySQL에 저장
 */
@Slf4j
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
     * @return 가입 시각(createdAt)만 포함한 응답
     */
    @Transactional
    public RegisterResponseDTO register(String idToken, RegisterRequestDTO request) {
        FirebaseToken decodedToken = verifyToken(idToken);

        String firebaseUid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        // 전화번호는 클라이언트가 인증한 값이 아니라 검증된 ID Token의 phone_number claim만 신뢰
        String phoneNumber = extractPhoneNumberClaim(decodedToken);

        ConflictGuard.requireUnique(
                userRepository.existsByFirebaseUid(firebaseUid), "이미 가입된 계정입니다");
        // email은 선택 필드(§3.1) — null이면 existsByEmail(null)이 SQL상 항상 false로 무력화되므로 의미 없는 호출을 스킵
        if (email != null) {
            ConflictGuard.requireUnique(
                    userRepository.existsByEmail(email), "이미 가입된 이메일입니다");
        }
        // 전화번호 중복 확인 (계정 고유성 앵커)
        ConflictGuard.requireUnique(
                userRepository.existsByPhoneNumber(phoneNumber), "이미 가입된 전화번호입니다");
        ConflictGuard.requireUnique(
                userRepository.existsByNickname(request.getNickname()), "이미 사용 중인 닉네임입니다");

        // User 엔티티 생성 및 저장
        User user = User.builder()
                .firebaseUid(firebaseUid)
                .email(email)
                .nickname(request.getNickname())
                .phoneNumber(phoneNumber)
                .instrument(request.getInstrument())
                .school(request.getSchool())
                .isGraduate(request.getIsGraduate())
                .build();

        try {
            // saveAndFlush로 즉시 flush시켜 UNIQUE 제약 위반을 이 catch 블록 안에서 잡는다.
            // save()만 쓰면 flush가 트랜잭션 커밋 시점(메서드 반환 후)까지 미뤄져 예외가
            // 이 try-catch 밖에서 터지고, 아래 Firebase 보상 롤백이 아예 실행되지 않는다.
            userRepository.saveAndFlush(user);
        } catch (Exception e) {
            // MySQL 저장 실패 시 Firebase 계정 롤백
            try {
                firebaseAuth.deleteUser(firebaseUid);
            } catch (FirebaseAuthException ex) {
                log.error("Firebase 계정 롤백 실패 — 고아 계정 발생 가능 (firebaseUid={})", firebaseUid, ex);
            }
            throw new RegistrationFailedException("회원가입 중 오류가 발생했습니다");
        }

        // 경력 저장
        if (request.getCareers() != null && !request.getCareers().isEmpty()) {
            List<Career> careers = request.getCareers().stream()
                    .map(dto -> Career.of(user, dto.getOrganization(), dto.getContexts()))
                    .collect(Collectors.toList());
            careerRepository.saveAll(careers);
        }

        return RegisterResponseDTO.from(user);
    }

    /*
     * FCM 토큰 갱신
     * 앱 실행 시 클라이언트에서 최신 FCM 토큰을 전송해 DB 업데이트
     *
     * @param user    현재 로그인한 유저(detached일 수 있음 — 내부에서 managed 재조회)
     * @param request FCM 토큰 갱신 요청 DTO
     */
    @Transactional
    public void updateFcmToken(User user, FCMTokenUpdateRequestDTO request) {
        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));

        managedUser.updateFcmToken(request.getFcmToken());
    }

    /*
     * 전화번호 갱신
     * register()와 동일하게 Authorization 헤더의 Firebase ID Token을 재검증해
     * 그 안의 phone_number claim만 신뢰(요청 바디는 받지 않음). 현재 번호와 같으면 아무 것도 하지 않음
     *
     * @param user    현재 로그인한 유저(detached일 수 있음 — 내부에서 managed 재조회)
     * @param idToken Firebase ID Token (Authorization 헤더에서 추출)
     */
    @Transactional
    public void updatePhoneNumber(User user, String idToken) {
        FirebaseToken decodedToken = verifyToken(idToken);
        String phoneNumber = extractPhoneNumberClaim(decodedToken);

        User managedUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));

        if (phoneNumber.equals(managedUser.getPhoneNumber())) {
            return;
        }

        ConflictGuard.requireUnique(
                userRepository.existsByPhoneNumber(phoneNumber), "이미 가입된 전화번호입니다");

        managedUser.updatePhoneNumber(phoneNumber);
    }

    // Firebase 토큰 검증 및 디코딩
    private FirebaseToken verifyToken(String idToken) {
        try {
            return firebaseAuth.verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new UnauthorizedException("유효하지 않은 Firebase 토큰입니다");
        }
    }

    // 검증된 토큰에서 phone_number claim 추출 (Firebase Admin SDK에 전용 getter 없어 claims map에서 직접 조회)
    private String extractPhoneNumberClaim(FirebaseToken decodedToken) {
        Object phoneClaim = decodedToken.getClaims().get("phone_number");
        if (phoneClaim == null) {
            throw new BadRequestException("휴대폰 인증 정보가 없습니다");
        }
        return phoneClaim.toString();
    }
}