package com.obri_back.obri.concert.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.obri_back.obri.concert.entity.Concert;

/*
 * Concert 저장소 — 동적 필터(JpaSpecificationExecutor) 제공
 */
public interface ConcertRepository extends JpaRepository<Concert, Long>, JpaSpecificationExecutor<Concert> {
    Optional<Concert> findByExternalId(String externalId); // KOPIS 동기화 신규/기존 판별
}
