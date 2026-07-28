package com.obri_back.obri.auth.dto;

import com.obri_back.obri.user.dto.CareerDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * 회원가입 요청 바디 (POST /api/auth/register)
 * Firebase 인증(UID·이메일·전화)은 토큰에서 취하고, 여기서는 추가 프로필만 받음
 * 필수 필드 검증 필요 — 누락 시 DB NOT NULL 위반으로 500이 나면서 Firebase 계정 보상 삭제까지 발동하므로
 * save() 이전(400)에 반드시 차단해야 함
 */
@Getter
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다")
    private String nickname;

    @NotBlank(message = "악기를 입력해주세요")
    private String instrument;

    @NotBlank(message = "학교를 입력해주세요")
    private String school;

    @NotNull(message = "졸업 여부를 입력해주세요")
    private Boolean isGraduate;

    private List<CareerDTO> careers;
}
