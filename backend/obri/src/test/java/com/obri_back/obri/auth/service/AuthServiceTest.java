package com.obri_back.obri.auth.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.obri_back.obri.auth.dto.FCMTokenUpdateRequestDTO;
import com.obri_back.obri.auth.dto.RegisterRequestDTO;
import com.obri_back.obri.global.exception.ConflictException;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.UnauthorizedException;
import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.repository.CareerRepository;
import com.obri_back.obri.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock FirebaseAuth firebaseAuth;
    @Mock UserRepository userRepository;
    @Mock CareerRepository careerRepository;

    @InjectMocks AuthService authService;

    private FirebaseToken mockToken;

    @BeforeEach
    void setUp() throws Exception {
        mockToken = mock(FirebaseToken.class);
        lenient().when(mockToken.getUid()).thenReturn("test-uid");
        lenient().when(mockToken.getEmail()).thenReturn("test@test.com");
        lenient().when(mockToken.getClaims())
                .thenReturn(Map.of("phone_number", "010-1234-5678"));
    }

    @Test
    void register_savesUserWhenValid() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(userRepository.existsByFirebaseUid("test-uid")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(userRepository.existsByPhoneNumber("010-1234-5678")).willReturn(false);
        given(userRepository.existsByNickname(any())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);
        given(request.getNickname()).willReturn("tester");
        given(request.getCareers()).willReturn(null);

        authService.register("valid-token", request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getPhoneNumber()).isEqualTo("010-1234-5678");
    }

    @Test
    void register_throwsUnauthorizedWhenTokenInvalid() throws Exception {
        given(firebaseAuth.verifyIdToken("invalid-token"))
                .willThrow(mock(FirebaseAuthException.class));

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);

        assertThatThrownBy(() -> authService.register("invalid-token", request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 Firebase 토큰입니다");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsConflictWhenUidExists() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(userRepository.existsByFirebaseUid("test-uid")).willReturn(true);

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);

        assertThatThrownBy(() -> authService.register("valid-token", request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 가입된 계정입니다");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_skipsEmailCheckWhenEmailIsNull() throws Exception {
        given(mockToken.getEmail()).willReturn(null);
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(userRepository.existsByFirebaseUid("test-uid")).willReturn(false);
        given(userRepository.existsByPhoneNumber("010-1234-5678")).willReturn(false);
        given(userRepository.existsByNickname(any())).willReturn(false);
        given(userRepository.save(any(User.class))).willAnswer(inv -> inv.getArgument(0));

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);
        given(request.getNickname()).willReturn("tester");
        given(request.getCareers()).willReturn(null);

        authService.register("valid-token", request);

        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void register_throwsConflictWhenNicknameExists() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(userRepository.existsByFirebaseUid("test-uid")).willReturn(false);

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);
        given(request.getNickname()).willReturn("duplicated");
        given(userRepository.existsByNickname("duplicated")).willReturn(true);

        assertThatThrownBy(() -> authService.register("valid-token", request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 사용 중인 닉네임입니다");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsBadRequestWhenPhoneNumberClaimMissing() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(mockToken.getClaims()).willReturn(Map.of());

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);

        assertThatThrownBy(() -> authService.register("valid-token", request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("휴대폰 인증 정보가 없습니다");

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_throwsConflictWhenPhoneNumberExists() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(userRepository.existsByFirebaseUid("test-uid")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(userRepository.existsByPhoneNumber("010-1234-5678")).willReturn(true);

        RegisterRequestDTO request = mock(RegisterRequestDTO.class);

        assertThatThrownBy(() -> authService.register("valid-token", request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 가입된 전화번호입니다");

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateFcmToken_updatesTokenWhenUserExists() {
        User user = mock(User.class);
        given(userRepository.findByFirebaseUid("test-uid")).willReturn(Optional.of(user));

        FCMTokenUpdateRequestDTO request = mock(FCMTokenUpdateRequestDTO.class);
        given(request.getFcmToken()).willReturn("new-fcm-token");

        authService.updateFcmToken("test-uid", request);

        verify(user, times(1)).updateFcmToken("new-fcm-token");
    }

    @Test
    void updateFcmToken_throwsNotFoundWhenUserMissing() {
        given(userRepository.findByFirebaseUid("missing-uid")).willReturn(Optional.empty());

        FCMTokenUpdateRequestDTO request = mock(FCMTokenUpdateRequestDTO.class);

        assertThatThrownBy(() -> authService.updateFcmToken("missing-uid", request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }

    @Test
    void updatePhoneNumber_updatesWhenValidAndDifferent() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);

        User user = mock(User.class);
        given(user.getPhoneNumber()).willReturn("010-0000-0000");
        given(userRepository.findByFirebaseUid("test-uid")).willReturn(Optional.of(user));
        given(userRepository.existsByPhoneNumber("010-1234-5678")).willReturn(false);

        authService.updatePhoneNumber("test-uid", "valid-token");

        verify(user, times(1)).updatePhoneNumber("010-1234-5678");
    }

    @Test
    void updatePhoneNumber_doesNothingWhenSameAsCurrent() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);

        User user = mock(User.class);
        given(user.getPhoneNumber()).willReturn("010-1234-5678");
        given(userRepository.findByFirebaseUid("test-uid")).willReturn(Optional.of(user));

        authService.updatePhoneNumber("test-uid", "valid-token");

        verify(user, never()).updatePhoneNumber(any());
        verify(userRepository, never()).existsByPhoneNumber(any());
    }

    @Test
    void updatePhoneNumber_throwsBadRequestWhenClaimMissing() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(mockToken.getClaims()).willReturn(Map.of());

        assertThatThrownBy(() -> authService.updatePhoneNumber("test-uid", "valid-token"))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("휴대폰 인증 정보가 없습니다");

        verify(userRepository, never()).findByFirebaseUid(any());
    }

    @Test
    void updatePhoneNumber_throwsConflictWhenPhoneNumberExists() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);

        User user = mock(User.class);
        given(user.getPhoneNumber()).willReturn("010-0000-0000");
        given(userRepository.findByFirebaseUid("test-uid")).willReturn(Optional.of(user));
        given(userRepository.existsByPhoneNumber("010-1234-5678")).willReturn(true);

        assertThatThrownBy(() -> authService.updatePhoneNumber("test-uid", "valid-token"))
                .isInstanceOf(ConflictException.class)
                .hasMessage("이미 가입된 전화번호입니다");

        verify(user, never()).updatePhoneNumber(any());
    }

    @Test
    void updatePhoneNumber_throwsUnauthorizedWhenTokenInvalid() throws Exception {
        given(firebaseAuth.verifyIdToken("invalid-token"))
                .willThrow(mock(FirebaseAuthException.class));

        assertThatThrownBy(() -> authService.updatePhoneNumber("test-uid", "invalid-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("유효하지 않은 Firebase 토큰입니다");

        verify(userRepository, never()).findByFirebaseUid(any());
    }

    @Test
    void updatePhoneNumber_throwsNotFoundWhenUserMissing() throws Exception {
        given(firebaseAuth.verifyIdToken("valid-token")).willReturn(mockToken);
        given(userRepository.findByFirebaseUid("missing-uid")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updatePhoneNumber("missing-uid", "valid-token"))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("유저를 찾을 수 없습니다");
    }
}