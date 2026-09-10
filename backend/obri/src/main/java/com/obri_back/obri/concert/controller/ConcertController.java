package com.obri_back.obri.concert.controller;

import com.obri_back.obri.concert.dto.ConcertResponseDTO;
import com.obri_back.obri.concert.kopis.KopisSyncService;
import com.obri_back.obri.concert.service.ConcertService;
import com.obri_back.obri.global.common.APIResponse;
import com.obri_back.obri.global.common.PageResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 연주회 관련 API 컨트롤러
 * GET  /api/concerts        — 연주회 전체 조회 (카테고리·지역·기간 필터·페이지네이션)
 * GET  /api/concerts/{id}   — 연주회 단건 조회
 * POST /api/concerts/sync   — KOPIS 동기화 수동 트리거 (개발/검증용, 정기 실행은 스케줄러가 담당)
 */
@RestController
@RequestMapping("/api/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertService concertService;
    private final KopisSyncService kopisSyncService;

    // 연주회 전체 조회 (카테고리·지역 필터 + 기간 + 페이지네이션, 기본 정렬은 공연 임박순. ?sort=로 재정의 가능)
    @GetMapping
    public ResponseEntity<APIResponse<PageResponse<ConcertResponseDTO>>> getConcertList(
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> region,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 10, sort = "startDate", direction = Sort.Direction.ASC) Pageable pageable) {

        Page<ConcertResponseDTO> response =
                concertService.getConcertList(category, region, fromDate, toDate, pageable);
        return ResponseEntity.ok(APIResponse.ok("연주회 목록 조회 성공", PageResponse.from(response)));
    }

    // 연주회 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ConcertResponseDTO>> getConcert(@PathVariable Long id) {
        ConcertResponseDTO response = concertService.getConcert(id);
        return ResponseEntity.ok(APIResponse.ok("연주회 조회 성공", response));
    }

    // KOPIS 동기화 수동 트리거 — 개발자 전용(Swagger/curl, 프론트 미호출).
    // 로그인한 사용자면 누구나 호출 가능(role 체계 없음, ConcoursController의 /crawl과 동일한 한계).
    // 중복 실행은 KopisSyncService에서 409로 차단
    @PostMapping("/sync")
    public ResponseEntity<APIResponse<Map<String, Integer>>> triggerSync() {
        int savedCount = kopisSyncService.sync();
        return ResponseEntity.ok(APIResponse.ok("KOPIS 동기화 완료", Map.of("savedCount", savedCount)));
    }
}
