package com.obri_back.obri.post.repository;

import com.obri_back.obri.post.entity.Post;
import com.obri_back.obri.post.entity.PostInstrument;
import com.obri_back.obri.post.entity.PostStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// GET /api/posts 필터: 필터 간 AND, 같은 필터 내 다중값은 OR
public class PostSpecification {

    public static Specification<Post> filter(List<String> categories, List<String> instruments,
            List<String> regions, LocalDate startDate, LocalDate endDate, PostStatus status) {
        return (root, query, cb) -> {
            query.distinct(true);
            List<Predicate> predicates = new ArrayList<>();

            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").in(categories));
            }

            if (instruments != null && !instruments.isEmpty()) {
                Join<Post, PostInstrument> instrumentJoin = root.join("postInstruments");
                predicates.add(instrumentJoin.get("instrument").in(instruments));
            }

            if (regions != null && !regions.isEmpty()) {
                // region 전용 필드가 없어 location 텍스트에 지역명이 포함되는지로 매칭
                List<Predicate> regionPredicates = regions.stream()
                        .map(region -> cb.like(root.get("location"), "%" + region + "%"))
                        .collect(Collectors.toList());
                predicates.add(cb.or(regionPredicates.toArray(new Predicate[0])));
            }

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("eventAt"), startDate.atStartOfDay()));
            }

            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("eventAt"), endDate.atTime(LocalTime.MAX)));
            }

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            } else {
                // 기본 노출은 OPEN·PARTIALLY_CLOSED, CLOSED는 status 필터로만 조회 가능
                predicates.add(root.get("status").in(List.of(PostStatus.OPEN, PostStatus.PARTIALLY_CLOSED)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
