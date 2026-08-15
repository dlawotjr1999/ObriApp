package com.obri_back.obri.concours.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * contest.co.kr에 대한 HTTP 접근만 담당(Jsoup 래핑). 파싱 로직은 여기 두지 않는다
 * ConcoursCrawlerService가 Mockito로 목킹할 수 있도록 별도 빈으로 분리
 */
@Component
public class ConcoursCrawlerClient {

    // list_n 뒤의 경로 세그먼트는 페이지가 아니라 카테고리 id(1=음악경연) — 실제 페이지네이션은 pg 쿼리 파라미터
    private static final String LIST_URL_TEMPLATE = "https://contest.co.kr/contest/list_n/1?s_area=&t=2&list_state=&limit_=1&ncount=50&pg=%d";
    private static final String USER_AGENT = "Mozilla/5.0 (compatible; ObriConcoursCrawler/1.0)";
    private static final int TIMEOUT_MS = 10_000;

    // 목록 페이지 문서 조회
    public Document fetchListDocument(int page) {
        return fetch(String.format(LIST_URL_TEMPLATE, page));
    }

    // 상세 페이지 문서 조회
    public Document fetchDetailDocument(String detailUrl) {
        return fetch(detailUrl);
    }

    private Document fetch(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent(USER_AGENT)
                    .timeout(TIMEOUT_MS)
                    .get();
        } catch (IOException e) {
            throw new ConcoursCrawlException("페이지 조회 실패: " + url, e);
        }
    }
}
