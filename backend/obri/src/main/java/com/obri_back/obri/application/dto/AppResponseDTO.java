package com.obri_back.obri.application.dto;

import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.entity.ApplicationStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppResponseDTO {
    private Long id;
    private ApplicationPostSummaryDTO post;
    // UserResponseDTO 대신 ApplicantResponseDTO 사용: 구인자에게 지원자의 email이 노출되는 것을 차단
    private ApplicantResponseDTO applicant;
    private String additionalInfo;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    public static AppResponseDTO from(Application application, User user) {
        return AppResponseDTO.builder()
                .id(application.getId())
                .post(ApplicationPostSummaryDTO.from(application.getPost()))
                .applicant(ApplicantResponseDTO.from(user))
                .additionalInfo(application.getAdditionalInfo())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
