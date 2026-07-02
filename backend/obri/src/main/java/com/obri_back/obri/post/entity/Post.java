package com.obri_back.obri.post.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.obri_back.obri.user.entity.User;

import jakarta.persistence.CascadeType;
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

import com.obri_back.obri.post.dto.PostCreateRequestDTO;
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
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

    public static Post create(User user, PostCreateRequestDTO dto) {
        Post post = new Post();
        post.user = user;
        post.title = dto.getTitle();
        post.category = dto.getCategory();
        post.eventAt = dto.getEventAt();
        post.location = dto.getLocation();
        post.timetable = dto.getTimetable();
        post.pay = dto.getPay();
        post.status = PostStatus.OPEN;
        return post;
    }

    public void addInstrument(PostInstrument instrument) {
        this.postInstruments.add(instrument);
    }

    public void updateInfo(PostCreateRequestDTO dto) {
        this.category = dto.getCategory();
        this.title = dto.getTitle();
        this.eventAt = dto.getEventAt();
        this.location = dto.getLocation();
        this.timetable = dto.getTimetable();
        this.pay = dto.getPay();
    }

    // 악기 목록 전체 교체 (career와 동일하게 전체 삭제 후 재삽입 패턴, orphanRemoval로 처리)
    public void replaceInstruments(List<PostInstrument> newInstruments) {
        this.postInstruments.clear();
        newInstruments.forEach(this::addInstrument);
    }

    public void close() {
        this.status = PostStatus.CLOSED;
    }
}
