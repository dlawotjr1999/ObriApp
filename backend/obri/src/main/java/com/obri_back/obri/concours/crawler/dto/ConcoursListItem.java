package com.obri_back.obri.concours.crawler.dto;

/*
 * 목록 페이지 한 행에서 파싱한 원시 정보. 상세 페이지 조회 전까지의 중간 값 객체
 */
public record ConcoursListItem(String title, String category, String organizer, String detailUrl) {
}
