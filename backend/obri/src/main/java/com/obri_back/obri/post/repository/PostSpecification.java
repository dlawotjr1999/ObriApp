package com.obri_back.obri.post.repository;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostInstrument;
import com.obri_back.obri.post.entity.PostStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/*
 * 구인글 목록 동적 필터 명세 빌더
 * GET /api/posts 필터: 필터 간 AND, 같은 필터 내 다중값은 OR
 * eventAt이 지난 글은 항상 제외(공연 종료 후 목록 노출 방지 — BACKLOG.md #8)
 * status는 필터 파라미터로 받지 않고 항상 OPEN·PARTIALLY_CLOSED만 노출 — CLOSED(마감)는 이 공개 목록에
 * 노출하지 않는다(BACKLOG.md #35). 작성자 본인의 마감글은 PostService.getMyPosts(status 필터 없음)로 조회
 */
public class PostSpecification {

    // 카테고리·악기·지역·기간 조건을 조합한 Specification 생성
    public static Specification<Post> filter(List<String> categories, List<String> instruments,
            List<String> regions, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            // 공연 날짜가 지난 글은 항상 제외 — status 필터로도 우회 불가
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventAt"), LocalDateTime.now()));

            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").in(categories));
            }

            if (instruments != null && !instruments.isEmpty()) {
                Join<Post, PostInstrument> instrumentJoin = root.join("postInstruments");
                predicates.add(instrumentJoin.get("instrument").in(instruments));
            }

            if (regions != null && !regions.isEmpty()) {
                predicates.add(root.get("region").in(regions));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventAt"), startDate.atStartOfDay()));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventAt"), endDate.atTime(LocalTime.MAX)));
            }

            // CLOSED(마감)는 공개 목록에 노출하지 않음 — 항상 OPEN·PARTIALLY_CLOSED만
            predicates.add(root.get("status").in(List.of(PostStatus.OPEN, PostStatus.PARTIALLY_CLOSED)));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
