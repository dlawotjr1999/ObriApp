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

@Getter
@NoArgsConstructor
@Entity
@Table(name = "Concours")
public class Concours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable=false)
    private String title;

    @Column(name="category", nullable=false)
    private String category;

    @Column(name="targetInstrument", nullable=false)
    private String targetInstrument;

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

    @Column(name="crawled_at", nullable=false)
    private LocalDateTime crawledAt;
}
