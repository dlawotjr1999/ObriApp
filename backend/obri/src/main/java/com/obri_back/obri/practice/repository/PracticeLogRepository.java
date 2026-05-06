package com.obri_back.obri.practice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.practice.entity.PracticeLog;

@Repository
public interface PracticeLogRepository extends JpaRepository<PracticeLog, Integer> {
    List<PracticeLog> findByUserIdOrderByLogDateDesc(String userId);
    List<PracticeLog> findByUserIdAndLogDateBetween(String userId, LocalDate start, LocalDate end);
}
