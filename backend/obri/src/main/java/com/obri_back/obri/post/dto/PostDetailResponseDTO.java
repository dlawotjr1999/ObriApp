package com.obri_back.obri.post.dto;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;
import com.obri_back.obri.user.entity.User;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// GET /api/posts/{id} 단건 조회 전용 — 목록 조회(PostSummaryResponseDTO)에는 없는
// writer, applicationCount, isMine, hasApplied 포함
@Getter
@Builder
public class PostDetailResponseDTO {
    private Long id;
    private Writer writer;
    private Long applicationCount;
    private Boolean isMine;
    private Boolean hasApplied;
    private String category;
    private String title;
    private LocalDateTime eventAt;
    private String location;
    private String timetable;
    private Integer pay;
    private PostStatus status;
    private List<PostInstrumentDTO> instruments;
    private LocalDateTime createdAt;

    // Post 엔티티 + 계산값(지원자 수·본인 여부·지원 여부) → 상세 DTO 변환
    public static PostDetailResponseDTO from(Post post, long applicationCount, boolean isMine, boolean hasApplied) {
        return PostDetailResponseDTO.builder()
                .id(post.getId())
                .writer(Writer.from(post.getUser()))
                .applicationCount(applicationCount)
                .isMine(isMine)
                .hasApplied(hasApplied)
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

    // 작성자 요약 정보 (닉네임·전공 악기만 노출)
    @Getter
    @Builder
    public static class Writer {
        private String nickname;
        private String instrument;

        // User 엔티티 → 작성자 요약 변환
        public static Writer from(User user) {
            return Writer.builder()
                    .nickname(user.getNickname())
                    .instrument(user.getInstrument())
                    .build();
        }
    }
}
