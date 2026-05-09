package com.obri_back.obri.user.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.user.entity.Career;

@Repository
public interface CareerRepository extends JpaRepository<Career, Long> {
    List<Career> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM Career c WHERE c.user.id = :userId")
    void deleteByUserId(Long userId);
}
