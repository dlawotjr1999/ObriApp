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

    @Test
    void confirmInstrument_throwsWhenInstrumentNotInPost() {
        assertThatThrownBy(() -> post.confirmInstrument("트럼펫"))
                .isInstanceOf(BadRequestException.class);
    }
}
