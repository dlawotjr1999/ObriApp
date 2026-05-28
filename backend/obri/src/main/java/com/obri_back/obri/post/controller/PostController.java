package com.obri_back.obri.post.controller;

import com.obri_back.obri.global.common.APIResponse;
import com.obri_back.obri.post.dto.PostCreateRequestDTO;
import com.obri_back.obri.post.dto.PostResponseDTO;
import com.obri_back.obri.post.service.PostService;
import com.obri_back.obri.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<APIResponse<PostResponseDTO>> createPost(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid PostCreateRequestDTO request) {
        PostResponseDTO response = postService.createPost(user, request);
        return ResponseEntity.ok(APIResponse.ok("구인글이 등록되었습니다", response));
    }
}
