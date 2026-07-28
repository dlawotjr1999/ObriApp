package com.obri_back.obri.user.service;

import com.obri_back.obri.global.exception.ConflictException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.user.dto.UserPublicProfileDTO;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.user.dto.SchoolEmailUpdateRequestDTO;
import com.obri_back.obri.user.dto.UserUpdateRequestDTO;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock CareerRepository careerRepository;

    @InjectMocks UserService userService;

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
    }

    @Test
    void getMyInfo_returnsUserWhenExists() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));

        UserResponseDTO result = userService.getMyInfo(1L);

        assertThat(result.getNickname()).isEqualTo("tester");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void getMyInfo_throwsNotFoundWhenMissing() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMyInfo(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }

    @Test
    void getUserProfile_returnsUserWhenExists() {
        given(userRepository.findByNickname("tester")).willReturn(Optional.of(mockUser));

        UserPublicProfileDTO result = userService.getUserProfile("tester");

        assertThat(result.getNickname()).isEqualTo("tester");
    }

    @Test
    void getUserProfile_throwsNotFoundWhenMissing() {
        given(userRepository.findByNickname("ghost")).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserProfile("ghost"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }

    @Test
    void updateMyInfo_throwsConflictWhenNicknameDuplicated() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));

        UserUpdateRequestDTO request = mock(UserUpdateRequestDTO.class);
        given(request.getNickname()).willReturn("duplicated");
        given(userRepository.existsByNickname("duplicated")).willReturn(true);

        assertThatThrownBy(() -> userService.updateMyInfo(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 사용 중인 닉네임입니다");
    }

    @Test
    void checkNickname_returnsTrueWhenDuplicated() {
        given(userRepository.existsByNickname("tester")).willReturn(true);

        boolean result = userService.checkNickname("tester");

        assertThat(result).isTrue();
    }

    @Test
    void checkNickname_returnsFalseWhenAvailable() {
        given(userRepository.existsByNickname("newname")).willReturn(false);

        boolean result = userService.checkNickname("newname");

        assertThat(result).isFalse();
    }

    @Test
    void deleteUser_deletesWhenExists() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    void deleteUser_throwsNotFoundWhenMissing() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }

    @Test
    void updateSchoolEmail_updatesWhenValidAndDifferent() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
        given(userRepository.existsBySchoolEmail("student@school.ac.kr")).willReturn(false);

        SchoolEmailUpdateRequestDTO request = mock(SchoolEmailUpdateRequestDTO.class);
        given(request.getSchoolEmail()).willReturn("student@school.ac.kr");

        userService.updateSchoolEmail(1L, request);

        assertThat(mockUser.getSchoolEmail()).isEqualTo("student@school.ac.kr");
        assertThat(mockUser.isSchoolEmailVerified()).isFalse();
    }

    @Test
    void updateSchoolEmail_doesNothingWhenSameAsCurrent() {
        mockUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .firebaseUid("test-uid")
                .phoneNumber("010-1234-5678")
                .schoolEmail("student@school.ac.kr")
                .nickname("tester")
                .instrument("바이올린")
                .school("서울대")
                .isGraduate(false)
                .build();
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));

        SchoolEmailUpdateRequestDTO request = mock(SchoolEmailUpdateRequestDTO.class);
        given(request.getSchoolEmail()).willReturn("student@school.ac.kr");

        userService.updateSchoolEmail(1L, request);

        verify(userRepository, never()).existsBySchoolEmail(any());
    }

    @Test
    void updateSchoolEmail_throwsConflictWhenAlreadyExists() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mockUser));
        given(userRepository.existsBySchoolEmail("dup@school.ac.kr")).willReturn(true);

        SchoolEmailUpdateRequestDTO request = mock(SchoolEmailUpdateRequestDTO.class);
        given(request.getSchoolEmail()).willReturn("dup@school.ac.kr");

        assertThatThrownBy(() -> userService.updateSchoolEmail(1L, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 등록된 학교 이메일입니다");
    }

    @Test
    void updateSchoolEmail_throwsNotFoundWhenMissing() {
        given(userRepository.findById(99L)).willReturn(Optional.empty());

        SchoolEmailUpdateRequestDTO request = mock(SchoolEmailUpdateRequestDTO.class);

        assertThatThrownBy(() -> userService.updateSchoolEmail(99L, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }
}