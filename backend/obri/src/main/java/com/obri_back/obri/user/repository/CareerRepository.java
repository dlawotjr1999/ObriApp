package com.obri_back.obri.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CareerRepository extends JpaRepository<Career, Integer> {
    List<Career> findByUserId(String userId);
}
