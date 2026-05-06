package com.obri_back.obri.auth.dto;

import com.obri_back.obri.user.dto.CareerDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class RegisterRequestDTO {
    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다")
    private String nickname;

    private String phoneNumber;
    private String instrument;
    private String school;
    private Boolean isGraduate;
    private List<CareerDTO> careers;
}
