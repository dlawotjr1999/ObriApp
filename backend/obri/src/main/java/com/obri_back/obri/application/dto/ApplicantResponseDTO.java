package com.obri_back.obri.application.dto;

import com.obri_back.obri.user.dto.CareerDTO;
import com.obri_back.obri.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

// 지원서 응답 내 지원자 공개 프로필 DTO
// UserResponseDTO를 그대로 쓰면 구인자 조회 시 지원자의 email·phoneNumber까지 노출되므로 별도 분리
@Getter
@Builder
public class ApplicantResponseDTO {
    private String nickname;
    private String instrument;
    private String school;
    private Boolean isGraduate;
    private List<CareerDTO> careers;

    public static ApplicantResponseDTO from(User user) {
        return ApplicantResponseDTO.builder()
                .nickname(user.getNickname())
                .instrument(user.getInstrument())
                .school(user.getSchool())
                .isGraduate(user.isGraduate())
                .careers(user.getCareers().stream()
                        .map(CareerDTO::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
