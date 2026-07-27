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

/*
 * 유저 엔티티
 * 내부 식별자(id)와 Firebase 외부 식별자(firebaseUid)를 분리 관리하며,
 * 계정 고유성 앵커는 phoneNumber(UNIQUE)·firebaseUid(UNIQUE). Career와 1:N 소유
 */
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

    // 이메일 UNIQUE: 계정당 유일. null 허용(전화 인증 등 email 부재 케이스), MySQL은 다중 NULL 허용
    @Column(name = "email", unique = true)
    private String email;

    // Firebase가 계정마다 발급하는 전역 유일 식별자. 조회 키이므로 UNIQUE는 정합성 필수 조건
    @Column(name = "firebase_uid", nullable = false, unique = true)
    private String firebaseUid;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    // 소속(학적) 증명용 학교 이메일 — 계정 고유성 앵커는 아니지만 UNIQUE로 부가 방어. 선택 필드
    @Column(name = "school_email", unique = true)
    private String schoolEmail;

    @Column(name = "school_email_verified", nullable = false)
    private boolean schoolEmailVerified;

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

    // User-Career 양방향 1:N (부모 저장/수정 시 cascade·orphanRemoval로 함께 처리)
    @Builder.Default
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
        cascade = CascadeType.ALL, orphanRemoval = true
    )
    private List<Career> careers = new ArrayList<>();

    // FCM 토큰 갱신 (앱 실행 시 최신 토큰 반영)
    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    // 전화번호 갱신 (PATCH /api/auth/phone-number — Firebase ID Token의 phone_number claim만 신뢰)
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // 학교 이메일 등록/변경 — 값이 바뀌면 재인증이 필요하므로 인증 상태를 리셋
    public void updateSchoolEmail(String schoolEmail) {
        this.schoolEmail = schoolEmail;
        this.schoolEmailVerified = false;
    }

    // 내 정보 수정: null이 아닌 필드만 선택적으로 반영 (phoneNumber는 전용 인증 엔드포인트로 이관되어 여기서 다루지 않음)
    public void updateInfo(UserUpdateRequestDTO request) {
        if (request.getNickname() != null) this.nickname = request.getNickname();
        if (request.getInstrument() != null) this.instrument = request.getInstrument();
        if (request.getSchool() != null) this.school = request.getSchool();
        if (request.getIsGraduate() != null) this.isGraduate = request.getIsGraduate();
    }
}
