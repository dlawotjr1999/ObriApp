package com.obri_back.obri.post.entity;

import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.post.dto.PostCreateRequestDTO;
import com.obri_back.obri.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// 악기별 confirmed/closed에 따른 글 전체 상태(OPEN/PARTIALLY_CLOSED/CLOSED) 파생 로직 검증
class PostTest {

    private Post post; // 바이올린 people=2, 첼로 people=1

    @BeforeEach
    void setUp() {
        User owner = User.builder().id(1L).nickname("owner").firebaseUid("owner-uid").build();
        PostCreateRequestDTO request = PostCreateRequestDTO.builder()
                .category("결혼")
                .title("결혼식 구인")
                .eventAt(LocalDateTime.of(2024, 5, 1, 14, 0))
                .location("서울 강남구")
                .timetable("본식 14:00")
                .pay(150000)
                .instruments(List.of(
                        PostCreateRequestDTO.InstrumentItem.builder().instrument("바이올린").people(2).build(),
                        PostCreateRequestDTO.InstrumentItem.builder().instrument("첼로").people(1).build()
                ))
                .build();

        post = Post.create(owner, request);
        request.getInstruments().forEach(item ->
                post.addInstrument(PostInstrument.of(post, item.getInstrument(), item.getPeople())));
    }

    private PostInstrument instrument(String name) {
        return post.getPostInstruments().stream()
                .filter(pi -> pi.getInstrument().equals(name)).findFirst().orElseThrow();
    }

    @Test
    void confirmInstrument_partialClose_setsPartiallyClosed() {
        post.confirmInstrument("첼로"); // people=1 → 즉시 마감

        assertThat(instrument("첼로").getClosed()).isTrue();
        assertThat(instrument("첼로").getConfirmed()).isEqualTo(1);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PARTIALLY_CLOSED);
    }

    @Test
    void confirmInstrument_partialFill_keepsOpenUntilCapacity() {
        post.confirmInstrument("바이올린"); // people=2, confirmed=1 → 아직 미마감

        assertThat(instrument("바이올린").getClosed()).isFalse();
        assertThat(post.getStatus()).isEqualTo(PostStatus.OPEN);
    }

    @Test
    void confirmInstrument_allClosed_setsClosed() {
        post.confirmInstrument("첼로");
        post.confirmInstrument("바이올린");
        post.confirmInstrument("바이올린"); // 바이올린 정원 도달

        assertThat(post.getStatus()).isEqualTo(PostStatus.CLOSED);
    }

    @Test
    void revokeInstrument_reopensFromClosed() {
        post.confirmInstrument("첼로");
        post.confirmInstrument("바이올린");
        post.confirmInstrument("바이올린"); // CLOSED

        post.revokeInstrument("바이올린"); // 바이올린 재오픈, 첼로는 여전히 마감

        assertThat(instrument("바이올린").getClosed()).isFalse();
        assertThat(instrument("바이올린").getConfirmed()).isEqualTo(1);
        assertThat(post.getStatus()).isEqualTo(PostStatus.PARTIALLY_CLOSED);
    }

    @Test
    void confirmInstrument_throwsWhenAlreadyClosed() {
        post.confirmInstrument("첼로"); // 마감됨

        assertThatThrownBy(() -> post.confirmInstrument("첼로"))
                .isInstanceOf(BadRequestException.class);
    }

    // 시나리오 1.4: 지원자 전공이 모집 목록에 없으면 수락 허용(자리 미반영, 상태만 변경) — 예외 아님
    @Test
    void confirmInstrument_nonRecruitedInstrument_keepsStatusAndSeats() {
        post.confirmInstrument("트럼펫"); // 모집 목록에 없는 악기 → 무동작

        assertThat(post.getStatus()).isEqualTo(PostStatus.OPEN);
        assertThat(instrument("바이올린").getConfirmed()).isEqualTo(0);
        assertThat(instrument("첼로").getConfirmed()).isEqualTo(0);
    }

    // 미반영 수락(모집 목록에 없는 악기)의 철회는 되돌릴 자리가 없으므로 무동작
    @Test
    void revokeInstrument_nonRecruitedInstrument_noOp() {
        post.revokeInstrument("트럼펫");

        assertThat(post.getStatus()).isEqualTo(PostStatus.OPEN);
        assertThat(instrument("바이올린").getConfirmed()).isEqualTo(0);
    }

    // BACKLOG.md #23: 이미 정원 마감된 악기는 지원 시점에 사전 차단하기 위한 조회용 메서드
    @Test
    void isInstrumentClosed_trueWhenInstrumentAlreadyClosed() {
        post.confirmInstrument("첼로"); // people=1 → 즉시 마감

        assertThat(post.isInstrumentClosed("첼로")).isTrue();
    }

    @Test
    void isInstrumentClosed_falseWhenInstrumentStillOpen() {
        post.confirmInstrument("바이올린"); // people=2, confirmed=1 → 아직 미마감

        assertThat(post.isInstrumentClosed("바이올린")).isFalse();
    }

    // 모집 목록에 없는 악기는 마감 개념이 없으므로 false(자리 미반영 지원 허용, 시나리오 1.4와 일관)
    @Test
    void isInstrumentClosed_falseWhenInstrumentNotRecruited() {
        assertThat(post.isInstrumentClosed("트럼펫")).isFalse();
    }
}
