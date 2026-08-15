package com.obri_back.obri.concours.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.obri_back.obri.concours.entity.Concours;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*
 * 콩쿠르 응답 DTO — 목록·단건 조회 공용
 * 크롤링 데이터라 필드 수가 적고 목록/단건 간 차이(endDate, sourceUrl)도 크지 않아 DTO를 나누지 않음
 * 날짜 필드는 Notion 명세·프론트 타입(ContestSummary 등)이 "YYYY-MM-DD"만 기대하므로 시각은 직렬화에서 제외
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcoursResponseDTO {
    private Long id;
    private String title;
    private String category;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime deadline;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;
    private String organizer;
    private String sourceUrl;

    // Concours 엔티티 → 응답 DTO 변환
    public static ConcoursResponseDTO from(Concours concours) {
        return ConcoursResponseDTO.builder()
                .id(concours.getId())
                .title(concours.getTitle())
                .category(concours.getCategory())
                .deadline(concours.getDeadline())
                .startDate(concours.getStartDate())
                .endDate(concours.getEndDate())
                .organizer(concours.getOrganizer())
                .sourceUrl(concours.getUrl())
                .build();
    }
}
