package com.obri_back.obri.application.dto;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApplicationPostSummaryDTO {
    private Long id;
    private String title;
    private String category;
    private LocalDateTime eventAt;
    private String location;
    private PostStatus status;

    public static ApplicationPostSummaryDTO from(Post post) {
        return ApplicationPostSummaryDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory())
                .eventAt(post.getEventAt())
                .location(post.getLocation())
                .status(post.getStatus())
                .build();
    }
}
