package com.obri_back.obri.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.CreationTimestamp;

import com.obri_back.obri.user.dto.UserUpdateRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, unique = true)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "firebase_uid", nullable = false)
    private String firebaseUid;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "instrument", nullable = false)
    private String instrument;

    @Column(name = "school", nullable = false)
    private String school;

    // 졸업 여부 (true: 졸업, false: 재학)
    @Column(name = "is_graduate", nullable = false)
    private boolean isGraduate;

    @Column(name = "fcm_token")
    private String fcmToken;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // User와 Career는 1:N 관계
    // career 필드를 통해 조회를 
    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
        cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<Career> careers = new ArrayList<>();

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateInfo(UserUpdateRequestDTO request) {
        if (request.getNickname() != null) this.nickname = request.getNickname();
        if (request.getPhoneNumber() != null) this.phoneNumber = request.getPhoneNumber();
        if (request.getInstrument() != null) this.instrument = request.getInstrument();
        if (request.getSchool() != null) this.school = request.getSchool();
        if (request.getIsGraduate() != null) this.isGraduate = request.getIsGraduate();
    }
}
