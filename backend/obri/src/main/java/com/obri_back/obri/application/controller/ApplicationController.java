package com.obri_back.obri.application.controller;

import com.obri_back.obri.application.dto.AppRequestDTO;
import com.obri_back.obri.application.dto.AppResponseDTO;
import com.obri_back.obri.application.dto.AppStatusUpdateDTO;
import com.obri_back.obri.application.service.ApplicationService;
import com.obri_back.obri.global.common.APIResponse;
import com.obri_back.obri.user.entity.User;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;

    // 지원서 제출
    @PostMapping("/submit")
    public ResponseEntity<APIResponse<AppResponseDTO>> submitApplication(
        @AuthenticationPrincipal User user,
        @RequestBody @Valid AppRequestDTO requestDto
    ) {
        applicationService.submitApplication(user, requestDto);
        return ResponseEntity.ok(APIResponse.ok("지원서가 제출되었습니다"));
    }

    // 지원서 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<AppResponseDTO>> getApplication(
        @AuthenticationPrincipal User user,
        @PathVariable Long id
    ) {
        AppResponseDTO response = applicationService.getApplication(user, id);
        return ResponseEntity.ok(APIResponse.ok("지원서를 조회했습니다", response));
    }

    // 지원 상태 업데이트
    @PatchMapping("/{id}/status")
    public ResponseEntity<APIResponse<AppResponseDTO>> updateApplicationStatus(
        @AuthenticationPrincipal User user,
        @PathVariable Long id,
        @RequestBody @Valid AppStatusUpdateDTO statusUpdateDto
    ) {
        applicationService.updateApplicationStatus(user, id, statusUpdateDto);
        return ResponseEntity.ok(APIResponse.ok("지원 상태가 업데이트되었습니다"));
    }

    // 한 게시글에 대한 지원서 목록 조회(post로 옮길 듯)
    // @GetMapping("/{postId}/applications")
    // public ResponseEntity<APIResponse<Page<AppResponseDTO>>> getApplicationsByPostId(
    //     @AuthenticationPrincipal User user,
    //     @PathVariable Long postId
    // ) {
    //     Page<AppResponseDTO> responses = applicationService.getApplicationsByPostId(postId, user, pageable);
    //     return ResponseEntity.ok(APIResponse.ok("게시글에 대한 지원서 목록을 조회했습니다", responses));
    // }
}
