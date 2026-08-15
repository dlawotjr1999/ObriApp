package com.obri_back.obri.concours.crawler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*
 * 콩쿠르 크롤링 스케줄러 — 매일 새벽 3시 자동 실행(Notion 명세: 새벽 시간대 권장)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConcoursCrawlerScheduler {

    private final ConcoursCrawlerService concoursCrawlerService;

    @Scheduled(cron = "0 0 3 * * *")
    public void runScheduledCrawl() {
        try {
            concoursCrawlerService.crawl();
        } catch (Exception e) {
            // 스케줄 실행 실패가 앱을 죽이지 않도록 격리하고 로그로만 남김
            log.error("콩쿠르 정기 크롤링 실패", e);
        }
    }
}
