package com.obri_back.obri.contest;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRepository extends JpaRepository<Contest, Integer> {
    List<Contest> findByCategory(String category);
    List<Contest> findByTargetInstrument(String instrument);
}
