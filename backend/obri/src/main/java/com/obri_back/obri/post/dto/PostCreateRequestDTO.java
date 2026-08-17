package com.obri_back.obri.post.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/*
 * 구인글 등록/수정 요청 바디 (POST·PUT /api/posts)
 * 모집 악기는 중첩 InstrumentItem 리스트로 받음
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequestDTO {

    @NotBlank
    private String category;

    @NotBlank
    private String title;

    @NotNull
    private LocalDateTime eventAt;

    @NotBlank
    private String location;

    @NotBlank
    private String timetable;

    @NotNull
    private Integer pay;

    // 구인글 상세 설명 (선택 입력) — BACKLOG.md #34
    private String description;

    @NotNull
    @Size(min = 1)
    @Valid
    private List<InstrumentItem> instruments;

    // 모집 악기 1건 (악기명 + 모집 인원)
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InstrumentItem {
        @NotBlank
        private String instrument;

        @NotNull
        @Min(1)
        private Integer people;
    }
}
