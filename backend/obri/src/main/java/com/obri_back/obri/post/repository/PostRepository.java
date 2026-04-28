package com.obri_back.obri.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByUserId(String userId);          // 유저가 작성한 구인글
    List<Post> findByStatus(PostStatus status);      // 상태별 구인글
}
