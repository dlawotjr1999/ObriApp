package com.obri_back.obri.concours.repository;

import com.obri_back.obri.concours.entity.Concours;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/*
 * 콩쿠르 목록 동적 필터 명세 빌더
 * GET /api/contests 필터: 필터 간 AND, 같은 필터 내 다중값은 OR
 */
public class ConcoursSpecification {

    // 카테고리·대상 악기 조건을 조합한 Specification 생성
    public static Specification<Concours> filter(List<String> categories, List<String> instruments) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("category").in(categories));
            }

            if (instruments != null && !instruments.isEmpty()) {
                predicates.add(root.get("targetInstrument").in(instruments));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
