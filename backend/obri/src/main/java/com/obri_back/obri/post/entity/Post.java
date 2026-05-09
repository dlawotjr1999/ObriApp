package com.obri_back.obri.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.obri_back.obri.user.entity.User;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table (name = "post")
public class Post {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)  
    private String title;

    @Column(name = "category", nullable = false)
    private String category;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostInstrument> postInstruments = new ArrayList<>();

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

    // 낙관적 락
    @Version
    @Column(name = "version")
    private Long version;                

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
