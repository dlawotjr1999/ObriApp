package com.obri_back.obri.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id", nullable = false, unique = true)
    private String id;

    @Column(name = "email")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phoneNumber", nullable = false)
    private String phoneNumber;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "instrument", nullable = false)
    private String instrument;

    @Column(name = "school", nullable = false)
    private String school;

    // 졸업 여부 (true: 졸업, false: 재학)
    // 추후 Enum.String으로 바꿀 수도?
    @Column(name = "is_graduate", nullable = false)
    private boolean isGraduate;

    @Column(name = "fcm_token")
    private String fcmToken;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // User와 Career는 1:N 관계
    // career 필드를 통해 조회를 위함
    @OneToMany(mappedBy = "user")
    private List<Career> careers = new ArrayList<>();
}
