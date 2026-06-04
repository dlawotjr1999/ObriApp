package com.obri_back.obri.application.dto;

import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.user.dto.UserResponseDTO;
import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.entity.ApplicationStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AppResponseDTO {
    private Long id;
    private Long postId;
    private UserResponseDTO applicant;
    private String additionalInfo;
    private ApplicationStatus status;
    private LocalDateTime createdAt;

    public static AppResponseDTO from(Application application, User user) {
        return AppResponseDTO.builder()
                .id(application.getId())
                .postId(application.getPost().getId())
                .applicant(UserResponseDTO.from(user))
                .additionalInfo(application.getAdditionalInfo())
                .status(application.getStatus())
                .createdAt(application.getCreatedAt())
                .build();
    }
}
