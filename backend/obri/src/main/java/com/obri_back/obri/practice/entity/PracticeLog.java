package com.obri_back.obri.practice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.obri_back.obri.user.entity.User;

/*
 * 연습 일지 엔티티 (User 종속)
 * 날짜·연습 시간(분)·코멘트를 기록. 연습 일지 기능(향후)의 기반 스캐폴딩
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "practice_log")
public class PracticeLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "log_date")
    private LocalDate logDate;

    @Column(name = "duration")
    private Integer duration; // 연습 시간 (분 단위)

    @Column(name = "content")
    private String content; // 연습 내용

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
