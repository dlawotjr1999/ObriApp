package com.obri_back.obri.contest;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 콩쿠르 엔티티 (독립 도메인)
 * 외부 사이트 크롤링으로 수집한 콩쿠르 일정 저장 — 향후 기능 스캐폴딩
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "contest")
public class Contest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category")
    private String category;

    @Column(name = "target_instrument")
    private String targetInstrument;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "organizer")
    private String organizer;

    @Column(name = "description")  
    private String description;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "crowled_at")
    private LocalDateTime crawledAt;
}
