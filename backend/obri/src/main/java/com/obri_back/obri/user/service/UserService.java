package com.obri_back.obri.user.service;

import com.obri_back.obri.global.exception.ConflictException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.post.dto.PostSummaryResponseDTO;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.user.dto.UserUpdateRequestDTO;
import com.obri_back.obri.user.entity.Career;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import com.obri_back.obri.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PostRepository postRepository;

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
     * 타인 프로필 조회
     *
     * @param nickname 조회할 유저의 닉네임
     * @return 유저 정보
     */
    @Transactional(readOnly = true)
    public UserResponseDTO getUserProfile(String nickname) {
        User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> new NotFoundException("유저를 찾을 수 없습니다"));
        return UserResponseDTO.from(user);
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
        if (request.getNickname() != null
                && !request.getNickname().equals(user.getNickname())
                && userRepository.existsByNickname(request.getNickname())) {
            throw new ConflictException("이미 사용 중인 닉네임입니다");
        }

        // 유저 정보 수정
        user.updateInfo(request);

        // 경력 전체 삭제 후 새로 insert
        if (request.getCareers() != null) {
            careerRepository.deleteByUserId(userId);
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
     * 닉네임 중복 체크
     *
     * @param nickname 중복 확인할 닉네임
     * @return 중복 여부
     */
    @Transactional(readOnly = true)
    public boolean checkNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /*
     * 내가 올린 구인글 목록 조회
     * 요약 정보만 반환 (카드 리스트용)
     *
     * @param userId   현재 로그인한 유저의 내부 ID
     * @param pageable 페이지네이션 정보
     * @return 구인글 요약 목록
     */
    @Transactional(readOnly = true)
    public Page<PostSummaryResponseDTO> getMyPosts(Long userId, Pageable pageable) {
        return postRepository.findByUserId(userId, pageable)
                .map(PostSummaryResponseDTO::from);
    }
}