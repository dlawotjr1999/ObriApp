package com.obri_back.obri.concert.entity;

import java.time.LocalDate;
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
 * 클래식 연주회 엔티티 — KOPIS(공연예술통합전산망) 오픈API 동기화 결과, 조회 전용 데이터.
 * 콩쿠르(Concours)와 달리 "접수 마감" 개념이 없고(이미 일정이 확정된 공연), 대신 공연장·지역·
 * 포스터가 새로 생겼다. externalId(KOPIS mt20id)를 upsert 기준 키로 쓴다.
 */
@Getter
@NoArgsConstructor
@Entity
@Table(name = "concert")
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // KOPIS 공연ID(mt20id) — 동기화 시 이 값 기준으로 upsert
    @Column(name = "external_id", nullable = false, unique = true)
    private String externalId;

    @Column(name = "title", nullable = false)
    private String title;

    // KOPIS 장르명(genrenm). shcate=CCCA(서양음악/클래식)로만 조회하므로 사실상 항상 같은 값이지만,
    // 추후 장르를 넓힐 가능성을 대비해 원본 값을 그대로 보관
    @Column(name = "category")
    private String category;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // 공연장(KOPIS fcltynm)
    @Column(name = "venue", nullable = false)
    private String venue;

    // 지역(KOPIS area) — 프론트 REGIONS 필터와 연결
    @Column(name = "region", nullable = false)
    private String region;

    // 포스터 이미지 URL(KOPIS poster) — 없는 공연도 있어 nullable
    @Column(name = "poster_url")
    private String posterUrl;

    // 원본 상세 페이지 링크 — KOPIS 응답에 없어 externalId로 직접 조립해서 저장
    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    // 동기화 결과로부터 신규 생성 — syncedAt은 저장 시점으로 고정
    public static Concert fromSync(String externalId, String title, String category, LocalDate startDate,
            LocalDate endDate, String venue, String region, String posterUrl, String url) {
        Concert concert = new Concert();
        concert.externalId = externalId;
        concert.title = title;
        concert.category = category;
        concert.startDate = startDate;
        concert.endDate = endDate;
        concert.venue = venue;
        concert.region = region;
        concert.posterUrl = posterUrl;
        concert.url = url;
        concert.syncedAt = LocalDateTime.now();
        return concert;
    }

    // 재동기화 시 갱신 — externalId(upsert 기준 키)는 불변이라 대상에서 제외
    public void updateFromSync(String title, String category, LocalDate startDate, LocalDate endDate,
            String venue, String region, String posterUrl, String url) {
        this.title = title;
        this.category = category;
        this.startDate = startDate;
        this.endDate = endDate;
        this.venue = venue;
        this.region = region;
        this.posterUrl = posterUrl;
        this.url = url;
        this.syncedAt = LocalDateTime.now();
    }
}
