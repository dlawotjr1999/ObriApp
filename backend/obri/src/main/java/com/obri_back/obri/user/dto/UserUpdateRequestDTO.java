package com.obri_back.obri.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 * 내 정보 수정 요청 바디 (PUT /api/users/me)
 * null 필드는 미변경, careers는 전체 교체
 */
@Getter
@NoArgsConstructor
public class UserUpdateRequestDTO {

    @Size(min = 2, max = 20, message = "닉네임은 2~20자 사이여야 합니다")
    private String nickname;

    private String instrument;
    private String school;
    private Boolean isGraduate;
    private List<CareerDTO> careers;  // id 있으면 수정, 없으면 추가
}