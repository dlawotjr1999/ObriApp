package com.obri_back.obri.concours.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.obri_back.obri.concours.entity.Concours;

/*
 * Concours 저장소 — 동적 필터(JpaSpecificationExecutor) 제공. 크롤러 전용 쓰기라 조회만 사용
 */
public interface ConcoursRepository extends JpaRepository<Concours, Long>, JpaSpecificationExecutor<Concours> {
}
