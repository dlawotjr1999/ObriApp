package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.crawler.dto.ConcoursDetailInfo;
import com.obri_back.obri.concours.crawler.dto.ConcoursListItem;
import com.obri_back.obri.concours.entity.Concours;
import com.obri_back.obri.concours.repository.ConcoursRepository;
import com.obri_back.obri.global.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * 콩쿠르 크롤링 오케스트레이션 — 목록을 1페이지부터 순회하며 신규 항목만 저장
 * title+url 기준 중복 체크, 한 페이지가 전부 중복이면 그 페이지에서 중단(신규 데이터는 항상 앞쪽 페이지에 나타난다는 전제)
 * 스케줄러·수동 트리거 엔드포인트가 공유하는 진입점이라 동시 실행은 AtomicBoolean으로 차단
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConcoursCrawlerService {

    // 안전판 — 사이트 전체 페이지 수 근방. 보통 중복 감지로 훨씬 일찍(1~2페이지) 멈춘다
    private static final int MAX_PAGE = 305;

    private final ConcoursCrawlerClient client;
    private final ConcoursRepository concoursRepository;

    private final AtomicBoolean running = new AtomicBoolean(false);

    // 크롤링 1회 실행 — 저장된 신규 건수 반환
    public int crawl() {
        if (!running.compareAndSet(false, true)) {
            throw new ConflictException("이미 콩쿠르 크롤링이 진행 중입니다");
        }

        try {
            return crawlPages();
        } finally {
            running.set(false);
        }
    }

    private int crawlPages() {
        int savedCount = 0;

        for (int page = 1; page <= MAX_PAGE; page++) {
            List<ConcoursListItem> items = fetchListItemsSafely(page);
            if (items.isEmpty()) {
                log.info("콩쿠르 크롤링 종료 — {}페이지에 항목 없음", page);
                break;
            }

            int newInPage = 0;
            for (ConcoursListItem item : items) {
                if (saveIfNew(item)) {
                    savedCount++;
                    newInPage++;
                }
            }

            if (newInPage == 0) {
                log.info("콩쿠르 크롤링 종료 — {}페이지가 전부 중복", page);
                break;
            }
        }

        log.info("콩쿠르 크롤링 완료 — 신규 {}건 저장", savedCount);
        return savedCount;
    }

    private List<ConcoursListItem> fetchListItemsSafely(int page) {
        try {
            Document document = client.fetchListDocument(page);
            return ConcoursListPageParser.parse(document);
        } catch (ConcoursCrawlException e) {
            log.warn("콩쿠르 목록 페이지 조회 실패, 크롤링 중단: page={}", page, e);
            return List.of();
        }
    }

    // 이미 존재하면 스킵(false), 신규면 상세 페이지까지 조회해 저장(true). 상세 파싱 실패는 해당 항목만 스킵
    private boolean saveIfNew(ConcoursListItem item) {
        if (concoursRepository.existsByTitleAndUrl(item.title(), item.detailUrl())) {
            return false;
        }

        try {
            Document detailDocument = client.fetchDetailDocument(item.detailUrl());
            ConcoursDetailInfo detail = ConcoursDetailPageParser.parse(detailDocument);
            concoursRepository.save(Concours.fromCrawl(
                    item.title(), item.category(), item.organizer(), item.detailUrl(),
                    detail.startDate(), detail.endDate(), detail.deadline()));
            return true;
        } catch (ConcoursCrawlException e) {
            log.warn("콩쿠르 상세 페이지 파싱 실패, 항목 스킵: {}", item.detailUrl(), e);
            return false;
        }
    }
}
