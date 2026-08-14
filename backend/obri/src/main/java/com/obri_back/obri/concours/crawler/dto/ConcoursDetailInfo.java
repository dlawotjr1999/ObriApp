package com.obri_back.obri.concours.crawler.dto;

import java.time.LocalDateTime;

/*
 * 상세 페이지 meta description에서 파싱한 정확한 날짜 정보
 */
public record ConcoursDetailInfo(LocalDateTime startDate, LocalDateTime endDate, LocalDateTime deadline) {
}
