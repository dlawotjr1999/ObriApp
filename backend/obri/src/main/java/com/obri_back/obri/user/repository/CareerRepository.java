package com.obri_back.obri.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.user.entity.Career;

/*
 * Career 저장소 — 유저별 경력 전체 삭제(수정 시 전체 삭제 후 재삽입 패턴) 제공
 */
@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {

    // 유저의 경력 전체 삭제 (정보 수정 시 재삽입 전 초기화)
    @Modifying
    @Query("DELETE FROM Career c WHERE c.user.id = :userId")
    void deleteByUserId(Long userId);
}
