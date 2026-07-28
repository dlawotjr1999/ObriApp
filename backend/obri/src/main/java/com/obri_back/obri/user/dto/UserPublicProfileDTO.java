package com.obri_back.obri.user.dto;

import com.obri_back.obri.user.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 타인 공개 프로필 DTO — email·phoneNumber 등 연락처는 제외
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicProfileDTO {
    private String nickname;
    private String instrument;
    private String school;
    private Boolean isGraduate;
    private LocalDateTime createdAt;
    private List<CareerDTO> careers;

    // User 엔티티 → 공개 프로필 DTO 변환 (연락처 제외)
    public static UserPublicProfileDTO from(User user) {
        return UserPublicProfileDTO.builder()
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
