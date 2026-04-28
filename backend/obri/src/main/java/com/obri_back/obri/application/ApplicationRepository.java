package com.obri_back.obri.application;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {
    List<Application> findByPostId(Long postId);     // 구인글의 지원자 목록
    List<Application> findByUserId(String userId);   // 유저의 지원 목록
    boolean existsByPostIdAndUserId(Long postId, String userId);  // 중복 지원 체크
}
