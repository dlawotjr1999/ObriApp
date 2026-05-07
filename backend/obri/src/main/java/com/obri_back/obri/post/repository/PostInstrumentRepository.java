package com.obri_back.obri.post.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.post.entity.PostInstrument;

@Repository
public interface PostInstrumentRepository extends JpaRepository<PostInstrument, Long> {
    List<PostInstrument> findByPostId(Long postId);  // 구인글의 악기 목록
}
