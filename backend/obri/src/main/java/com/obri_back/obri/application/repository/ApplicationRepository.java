package com.obri_back.obri.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.application.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    Page<Application> findByUserId(Long userId, Pageable pageable);
    Page<Application> findByPostId(Long postId, Pageable pageable);
    // DB UNIQUE 제약 전에 애플리케이션 레벨에서 먼저 차단해 409 에러 메시지를 제어
    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
