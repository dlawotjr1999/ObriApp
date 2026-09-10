package com.obri_back.obri.concours.service;

import com.obri_back.obri.concours.dto.ConcoursResponseDTO;
import com.obri_back.obri.concours.entity.Concours;
import com.obri_back.obri.concours.repository.ConcoursRepository;
import com.obri_back.obri.global.exception.NotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConcoursServiceTest {

    @Mock ConcoursRepository concoursRepository;
    @InjectMocks ConcoursService concoursService;

    private Concours sampleConcours() {
        return Concours.fromCrawl("콩쿠르", "클래식/실용", "주최사", "https://contest.co.kr/contest/view1/1",
                LocalDateTime.now(), LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(5));
    }

    @Test
    void getConcoursList_returnsPagedDtoList() {
        Page<Concours> page = new PageImpl<>(List.of(sampleConcours()), PageRequest.of(0, 10), 1);
        when(concoursRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Page<ConcoursResponseDTO> result = concoursService.getConcoursList(List.of("클래식/실용"), PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("콩쿠르");
    }

    @Test
    void getConcours_returnsDtoWhenFound() {
        when(concoursRepository.findById(1L)).thenReturn(Optional.of(sampleConcours()));

        ConcoursResponseDTO result = concoursService.getConcours(1L);

        assertThat(result.getTitle()).isEqualTo("콩쿠르");
        assertThat(result.getOrganizer()).isEqualTo("주최사");
    }

    @Test
    void getConcours_throwsNotFoundWhenMissing() {
        when(concoursRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> concoursService.getConcours(1L))
                .isInstanceOf(NotFoundException.class);
    }
}
