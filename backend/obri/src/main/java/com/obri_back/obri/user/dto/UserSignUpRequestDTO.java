// 회원가입 요청

package com.obri_back.obri.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  
public class UserSignUpRequestDTO {
    @NotBlank(message = "아이디를 입력해주세요")
    @Size(min = 6, max = 20, message = "아이디는 6~20자 사이여야 합니다")
    private Long id;

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식이 올바르지 않습니다")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "닉네임을 입력해주세요")
    private String nickname;

    private String phoneNumber;
    private String instrument;
    private String school;
}
