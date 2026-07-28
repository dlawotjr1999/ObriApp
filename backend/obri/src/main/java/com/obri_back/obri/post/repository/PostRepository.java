package com.obri_back.obri.post.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostStatus;

/*
 * Post 저장소 — 동적 필터(JpaSpecificationExecutor)·작성자별/상태별 조회 제공
 */
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {
    Page<Post> findByUserId(Long userId, Pageable pageable);      // 유저가 작성한 구인글
    List<Post> findByStatus(PostStatus status);      // 상태별 구인글

}
