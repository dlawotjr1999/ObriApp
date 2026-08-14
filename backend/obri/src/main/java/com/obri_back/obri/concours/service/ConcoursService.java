package com.obri_back.obri.concours.service;

import com.obri_back.obri.concours.dto.ConcoursResponseDTO;
import com.obri_back.obri.concours.entity.Concours;
import com.obri_back.obri.concours.repository.ConcoursRepository;
import com.obri_back.obri.concours.repository.ConcoursSpecification;
import com.obri_back.obri.global.exception.NotFoundException;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/*
 * 콩쿠르 관련 비즈니스 로직
 * 조회(전체/단건) 전용 — 데이터는 별도 크롤러가 채움
 */
@Service
@RequiredArgsConstructor
public class ConcoursService {

    private final ConcoursRepository concoursRepository;

    // 콩쿠르 전체 조회 — Specification 동적 필터 적용 후 DTO로 반환
    @Transactional(readOnly = true)
    public Page<ConcoursResponseDTO> getConcoursList(List<String> categories, Pageable pageable) {
        Specification<Concours> spec = ConcoursSpecification.filter(categories);
        return concoursRepository.findAll(spec, pageable).map(ConcoursResponseDTO::from);
    }

    // 콩쿠르 단건 조회
    @Transactional(readOnly = true)
    public ConcoursResponseDTO getConcours(Long concoursId) {
        Concours concours = concoursRepository.findById(concoursId)
                .orElseThrow(() -> new NotFoundException("콩쿠르를 찾을 수 없습니다"));
        return ConcoursResponseDTO.from(concours);
    }
}
