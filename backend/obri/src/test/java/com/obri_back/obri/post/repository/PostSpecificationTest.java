package com.obri_back.obri.post.repository;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostInfo;
import com.obri_back.obri.post.entity.PostInstrument;
import com.obri_back.obri.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// PostSpecification의 region 필터는 Criteria API 조건이라 실제 쿼리 실행 없이는 검증 불가 → @DataJpaTest(내장 H2)
// BACKLOG.md #38: location 자유텍스트 LIKE 매칭 대신 region 전용 컬럼 정확 일치로 전환
// globally_quoted_identifiers: `user` 테이블명이 H2 예약어(USER)와 충돌해 스키마 생성 자체가 실패하는 문제 회피
@DataJpaTest
@TestPropertySource(properties = "spring.jpa.properties.hibernate.globally_quoted_identifiers=true")
class PostSpecificationTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private PostRepository postRepository;

    private User owner;

    @BeforeEach
    void setUp() {
        owner = User.builder()
                .firebaseUid("owner-uid")
                .phoneNumber("010-0000-0000")
                .nickname("owner")
                .instrument("바이올린")
                .school("서울대")
                .build();
        entityManager.persist(owner);
    }

    private void persistPost(String region, String location) {
        Post post = Post.create(owner, PostInfo.builder()
                .category("결혼")
                .title("제목")
                .eventAt(LocalDateTime.now().plusDays(7))
                .location(location)
                .region(region)
                .timetable("13:00")
                .pay(100000)
                .build());
        post.addInstrument(PostInstrument.of(post, "바이올린", 1));
        entityManager.persist(post);
    }

    @Test
    void filter_matchesExactRegion() {
        persistPost("서울", "서울 강남구 OO홀");
        persistPost("경기", "경기 성남시 OO홀");

        List<Post> result = postRepository.findAll(
                PostSpecification.filter(null, null, List.of("서울"), null, null));

        assertThat(result).extracting(Post::getRegion).containsExactly("서울");
    }

    // location 텍스트에 지역명이 포함돼도 region 필드가 다르면 매칭되지 않아야 함 — LIKE 매칭 시절의 버그 재발 방지
    @Test
    void filter_doesNotMatchBySubstringOfLocation() {
        persistPost("경기", "서울 접경 경기 광주시 OO홀");

        List<Post> result = postRepository.findAll(
                PostSpecification.filter(null, null, List.of("서울"), null, null));

        assertThat(result).isEmpty();
    }

    @Test
    void filter_multipleRegionsActAsOr() {
        persistPost("서울", "서울 강남구 OO홀");
        persistPost("경기", "경기 성남시 OO홀");
        persistPost("부산", "부산 해운대구 OO홀");

        List<Post> result = postRepository.findAll(
                PostSpecification.filter(null, null, List.of("서울", "경기"), null, null));

        assertThat(result).extracting(Post::getRegion).containsExactlyInAnyOrder("서울", "경기");
    }

    @Test
    void filter_noRegionParam_returnsAllRegions() {
        persistPost("서울", "서울 강남구 OO홀");
        persistPost("경기", "경기 성남시 OO홀");

        List<Post> result = postRepository.findAll(PostSpecification.filter(null, null, null, null, null));

        assertThat(result).hasSize(2);
    }
}
