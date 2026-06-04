package com.obri_back.obri.user.service;

import com.obri_back.obri.global.exception.ConflictException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.user.dto.UserUpdateRequestDTO;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import com.obri_back.obri.post.repository.PostRepository;
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
    @Mock PostRepository postRepository;

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

        UserResponseDTO result = userService.getUserProfile("tester");

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
}