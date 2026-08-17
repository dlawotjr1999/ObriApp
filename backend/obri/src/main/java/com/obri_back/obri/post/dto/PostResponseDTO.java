package com.obri_back.obri.post.dto;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/*
 * 구인글 등록/수정 응답 DTO (생성·갱신 결과 반환용)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDTO {

    private Long id;
    private String category;
    private String title;
    private LocalDateTime eventAt;
    private String location;
    private String region;
    private String timetable;
    private Integer pay;
    private String description;
    private PostStatus status;
    private List<PostInstrumentDTO> instruments;
    private LocalDateTime createdAt;

    // Post 엔티티 → 응답 DTO 변환
    public static PostResponseDTO from(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .eventAt(post.getEventAt())
                .location(post.getLocation())
                .region(post.getRegion())
                .timetable(post.getTimetable())
                .pay(post.getPay())
                .description(post.getDescription())
                .status(post.getStatus())
                .instruments(post.getPostInstruments().stream()
                        .map(PostInstrumentDTO::from)
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
