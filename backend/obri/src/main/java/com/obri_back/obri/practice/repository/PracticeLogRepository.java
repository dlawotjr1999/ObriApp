package com.obri_back.obri.practice;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeLogRepository extends JpaRepository<PracticeLog, Integer> {
    List<PracticeLog> findByUserIdOrderByLogDateDesc(String userId);
    List<PracticeLog> findByUserIdAndLogDateBetween(String userId, LocalDate start, LocalDate end);
}
