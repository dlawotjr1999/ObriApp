package com.obri_back.obri.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.user.entity.Career;

@Repository
public interface CareerRepository extends JpaRepository<Career, Integer> {
    List<Career> findByUserId(String userId);
}
