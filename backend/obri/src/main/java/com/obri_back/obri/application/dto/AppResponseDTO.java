package com.obri_back.obri.application.dto;

import com.obri_back.obri.user.entity.User;
import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.entity.ApplicationStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/*
 * 지원서 응답 DTO — 전 엔드포인트 공통(글 요약 post + 지원자 요약 applicant 중첩)
 * 구인자는 applicant를, 지원자는 post를 소비 (관점별 비대칭 없이 단일 DTO로 통일)
 */
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

    // Application 엔티티 + 지원자 → 응답 DTO 변환 (글·지원자 요약 중첩)
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
