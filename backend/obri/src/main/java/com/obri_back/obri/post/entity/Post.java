package com.obri_back.obri.post;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.obri_back.obri.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table (name = "post")
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "title", nullable = false)  
    private String title;

    @CreationTimestamp
    @Column(name = "event_at", nullable = false)
    private LocalDateTime eventAt;

    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "timetable" , nullable = false)
    private String timetable;

    @Column(name = "pay", nullable = false)
    private Integer pay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PostStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
