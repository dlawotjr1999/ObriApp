package com.obri_back.obri.concours.entity;

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
 * 콩쿠르 엔티티 — 크롤러가 채우는 조회 전용 데이터, 다른 도메인과 연관관계 없음
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "concours")
public class Concours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable=false)
    private String title;

    @Column(name="category", nullable=false)
    private String category;

    @Column(name="startDate", nullable=false)
    private LocalDateTime startDate;

    @Column(name="endDate", nullable=false)
    private LocalDateTime endDate;

    @Column(name="deadline", nullable=false)
    private LocalDateTime deadline;

    @Column(name="organizer", nullable=false)
    private String organizer;

    @Column(name="url", nullable=false)
    private String url;

    @Column(name="crawledAt", nullable=false)
    private LocalDateTime crawledAt;

    // 크롤링 결과로부터 생성 — crawledAt은 저장 시점으로 고정
    public static Concours fromCrawl(String title, String category, String organizer, String url,
            LocalDateTime startDate, LocalDateTime endDate, LocalDateTime deadline) {
        Concours concours = new Concours();
        concours.title = title;
        concours.category = category;
        concours.organizer = organizer;
        concours.url = url;
        concours.startDate = startDate;
        concours.endDate = endDate;
        concours.deadline = deadline;
        concours.crawledAt = LocalDateTime.now();
        return concours;
    }
}
