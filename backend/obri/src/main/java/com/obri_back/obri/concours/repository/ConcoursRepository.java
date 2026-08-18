package com.obri_back.obri.concours.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.obri_back.obri.concours.entity.Concours;

/*
 * Concours 저장소 — 동적 필터(JpaSpecificationExecutor) 제공
 */
public interface ConcoursRepository extends JpaRepository<Concours, Long>, JpaSpecificationExecutor<Concours> {
    Optional<Concours> findByTitleAndUrl(String title, String url);    // 크롤러 신규/기존 판별 (title+source_url 기준)
}
