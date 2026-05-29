package com.obri_back.obri.application.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AppRequestDTO {
    private Long postId;
    private String additionalInfo;

    public static AppRequestDTO from(Long postId, String additionalInfo) {
        return AppRequestDTO.builder()
                .postId(postId)
                .additionalInfo(additionalInfo)
                .build();
    }
}
