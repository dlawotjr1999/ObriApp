package com.obri_back.obri.application.dto;

import lombok.Builder;
import lombok.Getter;

/*
 * 지원서 제출 요청 바디 (POST /api/applications/submit)
 * 지원자 프로필(악기·약력)은 토큰으로 조회하고 여기서는 대상 글·어필 문구만 받음
 */
@Getter
@Builder
public class AppRequestDTO {
    private Long postId;
    private String additionalInfo;

    // 필드로부터 요청 DTO 생성 (주로 테스트 편의용)
    public static AppRequestDTO from(Long postId, String additionalInfo) {
        return AppRequestDTO.builder()
                .postId(postId)
                .additionalInfo(additionalInfo)
                .build();
    }
}
