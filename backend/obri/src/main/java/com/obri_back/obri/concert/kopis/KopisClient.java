package com.obri_back.obri.concert.kopis;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
 * KOPIS(공연예술통합전산망) 오픈API에 대한 HTTP 접근만 담당(Jsoup 래핑). 파싱 로직은 여기 두지 않는다
 * KopisSyncService가 Mockito로 목킹할 수 있도록 별도 빈으로 분리(ConcoursCrawlerClient와 동일 역할 분리)
 * 응답이 XML이라 Jsoup을 HTML 파서가 아닌 XML 파서(Parser.xmlParser())로 사용
 */
@Component
public class KopisClient {

    private static final String LIST_URL_TEMPLATE =
            "http://www.kopis.or.kr/openApi/restful/pblprfr?service=%s&stdate=%s&eddate=%s&cpage=%d&rows=%d&shcate=CCCA";
    private static final int TIMEOUT_MS = 10_000;

    @Value("${kopis.service-key}")
    private String serviceKey;

    // 공연목록 조회. stdate/eddate는 "yyyyMMdd" 형식. shcate=CCCA(서양음악/클래식)로 고정 필터링
    public Document fetchListDocument(String stdate, String eddate, int page, int rows) {
        String url = String.format(LIST_URL_TEMPLATE, serviceKey, stdate, eddate, page, rows);
        try {
            return Jsoup.connect(url).parser(Parser.xmlParser()).timeout(TIMEOUT_MS).get();
        } catch (IOException e) {
            throw new KopisSyncException("KOPIS 공연목록 조회 실패: page=" + page, e);
        }
    }
}
