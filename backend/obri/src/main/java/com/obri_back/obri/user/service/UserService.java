package com.obri_back.obri.user.service;

import com.obri_back.obri.global.exception.ConflictGuard;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.user.dto.SchoolEmailUpdateRequestDTO;
import com.obri_back.obri.user.dto.UserPublicProfileDTO;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.user.dto.UserUpdateRequestDTO;
import com.obri_back.obri.user.entity.Career;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 유저 관련 비즈니스 로직 처리
 * 유저 정보 조회, 수정, 탈퇴 및 내 구인글/지원 목록 조회
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CareerRepository careerRepository;

    /*
     * 내 정보 조회
     *
     * @param userId 현재 로그인한 유저의 내부 ID
     * @return 유저 정보
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));
        return UserResponseDTO.from(user);
    }

    /*
     * managed User 재조회 — 다른 도메인 서비스가 detached 엔티티(예: 필터에서 온
     * @AuthenticationPrincipal User)를 넘겨받았을 때 LAZY 컬렉션 접근을 안전하게 하기 위한 진입점
     * (BACKLOG.md #1). 다른 도메인이 UserRepository를 직접 찌르지 않도록 이 메서드를 경유시킨다.
     *
     * @param userId 재조회할 유저의 내부 ID
     * @return managed 상태의 User 엔티티
     */
    @Transactional(readOnly = true)
    public User getManagedUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));
    }

    /*
     * 타인 프로필 조회
     * 공개 프로필이므로 email·phoneNumber는 노출하지 않음
     *
     * @param nickname 조회할 유저의 닉네임
     * @return 공개용 유저 정보
     */
    @Transactional(readOnly = true)
    public UserPublicProfileDTO getUserProfile(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));
        return UserPublicProfileDTO.from(user);
    }

    /*
     * 내 정보 수정
     * 수정 요청의 모든 필드를 한 번에 반영 (PUT 방식)
     * careers는 기존 데이터 전체 삭제 후 새로 insert
     *
     * @param userId  현재 로그인한 유저의 내부 ID
     * @param request 수정 요청 DTO
     * @return 수정된 유저 정보
     */
    @Transactional
    public UserResponseDTO updateMyInfo(Long userId, UserUpdateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));

        // 닉네임 변경 시 중복 체크
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            ConflictGuard.requireUnique(
                    userRepository.existsByNickname(request.getNickname()), "이미 사용 중인 닉네임입니다");
        }

        // 유저 정보 수정
        user.updateInfo(request);

        // 경력 전체 삭제 후 새로 insert
        if (request.getCareers() != null) {
            careerRepository.deleteByUserId(userId);
            List<Career> careers = request.getCareers().stream()
                    .map(dto -> Career.of(user, dto.getOrganization(), dto.getContexts()))
                    .collect(Collectors.toList());
            careerRepository.saveAll(careers);
        }

        return UserResponseDTO.from(user);
    }

    /*
     * 회원 탈퇴
     * MySQL 유저 삭제 (Firebase 계정 삭제는 클라이언트에서 처리)
     *
     * @param userId 현재 로그인한 유저의 내부 ID
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));
        userRepository.delete(user);
    }

    /*
     * 학교 이메일 등록/변경
     * 소속(학적) 증명 목적 — 저장만 하고 미인증 상태(schoolEmailVerified=false)로 둠
     * 현재 값과 같으면 아무 것도 하지 않음
     *
     * @param userId  현재 로그인한 유저의 내부 ID
     * @param request 학교 이메일 요청 DTO
     */
    @Transactional
    public void updateSchoolEmail(Long userId, SchoolEmailUpdateRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));

        String schoolEmail = request.getSchoolEmail();
        if (schoolEmail.equals(user.getSchoolEmail())) {
            return;
        }

        ConflictGuard.requireUnique(
                userRepository.existsBySchoolEmail(schoolEmail), "이미 등록된 학교 이메일입니다");

        user.updateSchoolEmail(schoolEmail);
    }

    /*
     * 닉네임 중복 체크
     *
     * @param nickname 중복 확인할 닉네임
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }
}