package com.obri_back.obri.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostInstrumentRepository extends JpaRepository<PostInstrument, Integer> {
    List<PostInstrument> findByPostId(Long postId);  // 구인글의 악기 목록
}
