package com.obri_back.obri.concours.controller;

import com.obri_back.obri.concours.crawler.ConcoursCrawlerService;
import com.obri_back.obri.concours.dto.ConcoursResponseDTO;
import com.obri_back.obri.concours.service.ConcoursService;
import com.obri_back.obri.global.config.SecurityConfig;
import com.obri_back.obri.global.exception.NotFoundException;
import com.obri_back.obri.global.security.FirebaseAuthFilter;
import com.obri_back.obri.user.entity.User;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConcoursController.class)
@Import(SecurityConfig.class)
class ConcoursControllerTest {

    @Autowired MockMvc mockMvc;
    @MockitoBean ConcoursService concoursService;
    @MockitoBean ConcoursCrawlerService concoursCrawlerService;
    @MockitoBean FirebaseAuthFilter firebaseAuthFilter;

    private Authentication auth;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            FilterChain chain = invocation.getArgument(2, FilterChain.class);
            chain.doFilter(
                    invocation.getArgument(0, ServletRequest.class),
                    invocation.getArgument(1, ServletResponse.class)
            );
            return null;
        }).when(firebaseAuthFilter).doFilter(any(), any(), any());

        User mockUser = User.builder()
                .id(1L)
                .email("test@test.com")
                .firebaseUid("test-uid")
                .nickname("tester")
                .instrument("바이올린")
                .school("서울대")
                .isGraduate(false)
                .build();

        auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
    }

    private ConcoursResponseDTO sampleDto() {
        return ConcoursResponseDTO.builder()
                .id(1L)
                .title("콩쿠르")
                .category("클래식/실용")
                .organizer("주최사")
                .sourceUrl("https://contest.co.kr/contest/view1/1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(10))
                .deadline(LocalDateTime.now().plusDays(5))
                .build();
    }

    @Test
    void getConcoursList_returns200WithPageResponse() throws Exception {
        Page<ConcoursResponseDTO> page = new PageImpl<>(List.of(sampleDto()), PageRequest.of(0, 10), 1);
        when(concoursService.getConcoursList(any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/contests").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].title").value("콩쿠르"))
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.currentPage").value(0));
    }

    @Test
    void getConcours_returns200() throws Exception {
        when(concoursService.getConcours(1L)).thenReturn(sampleDto());

        mockMvc.perform(get("/api/contests/1").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("콩쿠르"));
    }

    @Test
    void getConcours_returns404WhenNotFound() throws Exception {
        when(concoursService.getConcours(999L)).thenThrow(new NotFoundException("콩쿠르를 찾을 수 없습니다"));

        mockMvc.perform(get("/api/contests/999").with(authentication(auth)))
                .andExpect(status().isNotFound());
    }

    @Test
    void triggerCrawl_returns200WithSavedCount() throws Exception {
        when(concoursCrawlerService.crawl(null)).thenReturn(3);

        mockMvc.perform(post("/api/contests/crawl").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.savedCount").value(3));
    }

    @Test
    void triggerCrawl_passesMaxPagesQueryParamToService() throws Exception {
        when(concoursCrawlerService.crawl(2)).thenReturn(1);

        mockMvc.perform(post("/api/contests/crawl").param("maxPages", "2").with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.savedCount").value(1));

        verify(concoursCrawlerService).crawl(eq(2));
    }
}
