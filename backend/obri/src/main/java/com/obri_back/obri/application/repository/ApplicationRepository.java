package com.obri_back.obri.application.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.obri_back.obri.application.entity.Application;
import com.obri_back.obri.application.entity.ApplicationStatus;

/*
 * Application 저장소 — 지원자별/구인글별 조회, 중복 지원 체크, 알림 대상 토큰 조회 제공
 */
@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    // 구인글 수정 알림 대상(지정 상태) 지원자의 fcm_token 조회 — 토큰 없는 유저는 제외
    @Query("SELECT a.user.fcmToken FROM Application a " +
           "WHERE a.post.id = :postId AND a.status IN :statuses AND a.user.fcmToken IS NOT NULL")
    List<String> findApplicantFcmTokens(@Param("postId") Long postId,
                                        @Param("statuses") Collection<ApplicationStatus> statuses);

    // 목록 응답(AppResponseDTO)이 post·user를 매핑하므로 to-one 연관을 함께 로딩해 N+1 방지
    @EntityGraph(attributePaths = {"post", "user"})
    Page<Application> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"post", "user"})
    Page<Application> findByPostId(Long postId, Pageable pageable);
    // DB UNIQUE 제약 전에 애플리케이션 레벨에서 먼저 차단해 409 에러 메시지를 제어
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
    void deleteByPostId(Long postId);
}
