package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.crawler.dto.ConcoursDetailInfo;
import com.obri_back.obri.concours.crawler.dto.ConcoursListItem;
import com.obri_back.obri.concours.entity.Concours;
import com.obri_back.obri.concours.repository.ConcoursRepository;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

/*
 * 콩쿠르 크롤링 오케스트레이션 — 목록 전 페이지를 순회하며 신규 항목만 저장
 * 목록 정렬이 접수마감일 기준이라 신규 항목이 뒤쪽 페이지에도 나타날 수 있어, 페이지 단위 조기 종료는 하지 않는다
 * (구버전은 "한 페이지가 전부 중복이면 중단"했으나 실측 결과 그 전제가 틀려 제거됨 — BACKLOG #42)
 * title+url 기준 중복 체크로 이미 저장된 항목은 상세 페이지 재조회 없이 스킵
 * 스케줄러·수동 트리거 엔드포인트가 공유하는 진입점이라 동시 실행은 AtomicBoolean으로 차단
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConcoursCrawlerService {

    // 안전판 — 총 페이지 파싱이 비정상적으로 크게 나올 경우에 대비한 상한(사이트 전체는 현재 ~305페이지)
    private static final int HARD_CAP_PAGE = 1000;
    // 대상 사이트에 대한 예의상 지연 — 페이지 요청 사이에만 적용
    private static final long REQUEST_DELAY_MS = 300;

    private final ConcoursCrawlerClient client;
    private final ConcoursRepository concoursRepository;

    private final AtomicBoolean running = new AtomicBoolean(false);

    // 크롤링 1회 실행(전체 페이지) — 스케줄러 진입점, 저장된 신규 건수 반환
    public int crawl() {
        return crawl(null);
    }

    // 크롤링 1회 실행 — maxPages가 주어지면 그 페이지 수까지만 순회(개발자 수동 검증용, null이면 전체)
    public int crawl(Integer maxPages) {
        if (maxPages != null && maxPages <= 0) {
            throw new BadRequestException("maxPages는 1 이상이어야 합니다");
        }
        if (!running.compareAndSet(false, true)) {
            throw new ConflictException("이미 콩쿠르 크롤링이 진행 중입니다");
        }

        try {
            return crawlPages(maxPages);
        } finally {
            running.set(false);
        }
    }

    private int crawlPages(Integer maxPages) {
        Document firstPage = fetchListDocumentSafely(1);
        if (firstPage == null) {
            log.warn("콩쿠르 크롤링 중단 — 1페이지 조회 실패");
            return 0;
        }

        int totalPages = Math.min(ConcoursListPageParser.parseTotalPages(firstPage), HARD_CAP_PAGE);
        if (maxPages != null) {
            totalPages = Math.min(totalPages, maxPages);
        }
        log.info("콩쿠르 크롤링 시작 — 총 {}페이지", totalPages);

        int savedCount = saveItemsOnPage(firstPage);

        for (int page = 2; page <= totalPages; page++) {
            sleepBetweenRequests();

            Document document = fetchListDocumentSafely(page);
            if (document == null) {
                continue; // 해당 페이지만 스킵, 나머지 페이지는 계속 순회
            }
            savedCount += saveItemsOnPage(document);
        }

        log.info("콩쿠르 크롤링 완료 — 신규 {}건 저장", savedCount);
        return savedCount;
    }

    private int saveItemsOnPage(Document document) {
        int saved = 0;
        for (ConcoursListItem item : ConcoursListPageParser.parse(document)) {
            if (saveIfNew(item)) {
                saved++;
            }
        }
        return saved;
    }

    private Document fetchListDocumentSafely(int page) {
        try {
            return client.fetchListDocument(page);
        } catch (ConcoursCrawlException e) {
            log.warn("콩쿠르 목록 페이지 조회 실패, 해당 페이지 스킵: page={}", page, e);
            return null;
        }
    }

    private void sleepBetweenRequests() {
        try {
            Thread.sleep(REQUEST_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
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
