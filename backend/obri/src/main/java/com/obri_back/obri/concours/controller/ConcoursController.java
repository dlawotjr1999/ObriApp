package com.obri_back.obri.concours.controller;

import com.obri_back.obri.concours.crawler.ConcoursCrawlerService;
import com.obri_back.obri.concours.dto.ConcoursResponseDTO;
import com.obri_back.obri.concours.service.ConcoursService;
import com.obri_back.obri.global.common.APIResponse;
import com.obri_back.obri.global.common.PageResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 콩쿠르 관련 API 컨트롤러
 * GET  /api/contests        — 콩쿠르 전체 조회 (카테고리 필터·페이지네이션)
 * GET  /api/contests/{id}   — 콩쿠르 단건 조회
 * POST /api/contests/crawl  — 크롤링 수동 트리거 (개발/검증용, 정기 실행은 스케줄러가 담당)
 */
@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ConcoursController {

    private final ConcoursService concoursService;
    private final ConcoursCrawlerService concoursCrawlerService;

    // 콩쿠르 전체 조회 (카테고리 필터 + 페이지네이션, 정렬은 ?sort=deadline,asc 등 Pageable 기본 파라미터 사용)
    @GetMapping
    public ResponseEntity<APIResponse<PageResponse<ConcoursResponseDTO>>> getConcoursList(
            @RequestParam(required = false) List<String> category,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ConcoursResponseDTO> response = concoursService.getConcoursList(category, pageable);
        return ResponseEntity.ok(APIResponse.ok("콩쿠르 목록 조회 성공", PageResponse.from(response)));
    }

    // 콩쿠르 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ConcoursResponseDTO>> getConcours(@PathVariable Long id) {
        ConcoursResponseDTO response = concoursService.getConcours(id);
        return ResponseEntity.ok(APIResponse.ok("콩쿠르 조회 성공", response));
    }

    // 크롤링 수동 트리거 — 로그인한 사용자면 누구나 호출 가능(role 체계 없음, 백로그 참고). 중복 실행은 서비스에서 409 차단
    @PostMapping("/crawl")
    public ResponseEntity<APIResponse<Map<String, Integer>>> triggerCrawl() {
        int savedCount = concoursCrawlerService.crawl();
        return ResponseEntity.ok(APIResponse.ok("콩쿠르 크롤링 완료", Map.of("savedCount", savedCount)));
    }
}
