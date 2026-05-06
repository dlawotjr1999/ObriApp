package com.obri_back.obri.practice.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.practice.entity.PracticeFeedback;

@Repository
public interface PracticeFeedbackRepository extends JpaRepository<PracticeFeedback, Integer> {
    Optional<PracticeFeedback> findByUserIdAndPeriodStartAndPeriodEnd(
        String userId, LocalDate start, LocalDate end);   
}
