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
}
