package com.obri_back.obri.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 학교 이메일 등록/변경 요청 바디 (PATCH /api/users/me/school-email)
 * 저장만 하고 미인증 상태로 둠 — 인증은 별도 엔드포인트
 */
@Getter
@NoArgsConstructor
public class SchoolEmailUpdateRequestDTO {
    @NotBlank(message = "학교 이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String schoolEmail;
}
