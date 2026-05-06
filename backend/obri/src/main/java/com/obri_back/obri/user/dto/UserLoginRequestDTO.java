// 로그인 요청

package com.obri_back.obri.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserLoginRequestDTO {
    @NotBlank(message = "아이디를 입력해주세요")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
