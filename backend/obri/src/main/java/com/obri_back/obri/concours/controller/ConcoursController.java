package com.obri_back.obri.concours.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 콩쿠르 관련 API 컨트롤러
 * GET /api/contests      — 콩쿠르 전체 조회 (카테고리·악기 필터·페이지네이션)
 * GET /api/contests/{id} — 콩쿠르 단건 조회
 */
@RestController
@RequestMapping("/api/contests")
@RequiredArgsConstructor
public class ConcoursController {

    private final ConcoursService concoursService;

    // 콩쿠르 전체 조회 (카테고리·악기 필터 + 페이지네이션, 정렬은 ?sort=deadline,asc 등 Pageable 기본 파라미터 사용)
    @GetMapping
    public ResponseEntity<APIResponse<PageResponse<ConcoursResponseDTO>>> getConcoursList(
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> instrument,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<ConcoursResponseDTO> response = concoursService.getConcoursList(category, instrument, pageable);
        return ResponseEntity.ok(APIResponse.ok("콩쿠르 목록 조회 성공", PageResponse.from(response)));
    }

    // 콩쿠르 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ConcoursResponseDTO>> getConcours(@PathVariable Long id) {
        ConcoursResponseDTO response = concoursService.getConcours(id);
        return ResponseEntity.ok(APIResponse.ok("콩쿠르 조회 성공", response));
    }
}
