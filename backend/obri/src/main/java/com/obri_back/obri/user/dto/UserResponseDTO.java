package com.obri_back.obri.user.dto;

import com.obri_back.obri.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String email;
    private String phoneNumber;
    private String nickname;
    private String instrument;
    private String school;
    private Boolean isGraduate;
    private LocalDateTime createdAt;
    private List<CareerDTO> careers;

    public static UserResponseDTO from(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .nickname(user.getNickname())
                .instrument(user.getInstrument())
                .school(user.getSchool())
                .isGraduate(user.isGraduate())
                .createdAt(user.getCreatedAt())
                .careers(user.getCareers().stream()
                        .map(CareerDTO::from)
                        .collect(Collectors.toList()))
                .build();
        }
}
