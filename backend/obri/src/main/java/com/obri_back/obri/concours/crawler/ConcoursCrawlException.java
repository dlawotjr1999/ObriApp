package com.obri_back.obri.concours.crawler;

/*
 * 크롤링·파싱 실패 시 발생 (네트워크 오류, 사이트 마크업 변경으로 예상 패턴을 못 찾은 경우 등)
 * 호출부(ConcoursCrawlerService)가 항목 단위로 잡아 로그만 남기고 다음 항목으로 진행
 */
public class ConcoursCrawlException extends RuntimeException {
    public ConcoursCrawlException(String message) {
        super(message);
    }

    public ConcoursCrawlException(String message, Throwable cause) {
        super(message, cause);
    }
}
