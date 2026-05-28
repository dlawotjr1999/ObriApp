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
    private String timetable;
    private Integer pay;
    private PostStatus status;
    private List<PostInstrumentDTO> instruments;
    private LocalDateTime createdAt;

    public static PostResponseDTO from(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .category(post.getCategory())
                .title(post.getTitle())
                .eventAt(post.getEventAt())
                .location(post.getLocation())
                .timetable(post.getTimetable())
                .pay(post.getPay())
                .status(post.getStatus())
                .instruments(post.getPostInstruments().stream()
                        .map(PostInstrumentDTO::from)
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .build();
    }
}
