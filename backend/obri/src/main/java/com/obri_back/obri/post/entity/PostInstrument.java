package com.obri_back.obri.post.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * 구인글 모집 악기 엔티티 (Post 종속, 양방향 1:N의 자식)
 * 악기별 모집 인원(people)·확정 인원(confirmed)·마감 여부(closed)를 추적하며,
 * 악기 단위 @Version 낙관적 락으로 동시 수락 경합을 방지
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "post_instrument")
public class PostInstrument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "instrument", nullable = false)
    private String instrument;

    @Column(name = "people", nullable = false)
    private Integer people;

    @Column(name = "confirmed", nullable = false)
    private Integer confirmed;

    @Column(name = "closed", nullable = false)
    private Boolean closed;

    // 낙관적 락: 같은 Post 내 다른 악기끼리는 경합하지 않도록 악기 단위로 버전 관리
    @Version
    @Column(name = "version")
    private Long version;

    // 모집 악기 생성 (확정 인원 0, 미마감 상태로 초기화)
    public static PostInstrument of(Post post, String instrument, int people) {
        PostInstrument pi = new PostInstrument();
        pi.post = post;
        pi.instrument = instrument;
        pi.people = people;
        pi.confirmed = 0;
        pi.closed = false;
        return pi;
    }

    // 지원 수락: 확정 인원 1 증가, 정원 도달 시 악기 마감
    public void confirm() {
        this.confirmed++;
        if (this.confirmed >= this.people) {
            this.closed = true;
        }
    }

    // 수락 철회: 확정 인원 1 감소, 악기 마감 해제(재오픈)
    public void revoke() {
        if (this.confirmed > 0) {
            this.confirmed--;
        }
        this.closed = false;
    }

    // 정원 변경 반영(글 수정 시 이름이 같은 악기 병합용): 확정 인원은 유지하고, 새 정원 기준으로 마감 여부만 재계산
    public void updatePeople(int people) {
        this.people = people;
        this.closed = this.confirmed >= this.people;
    }
}
