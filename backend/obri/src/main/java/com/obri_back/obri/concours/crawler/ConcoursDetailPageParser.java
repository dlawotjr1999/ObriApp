package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.crawler.dto.ConcoursDetailInfo;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 콩쿠르 상세 페이지 파싱 — meta[name=description]의 "대회일 : ~, 접수일 : ~" 요약 문구에서 날짜 추출
 * 목록 페이지의 D-day 칸에는 정확한 날짜가 없어, 신규 항목에 한해 상세 페이지를 이 파서로 한 번 더 읽는다
 */
public class ConcoursDetailPageParser {

    // 예: "대회일 : 2026-06-14 ~ 2027-01-09, 접수일 : 2026-07-30 ~ 2027-01-09 23:59:59"
    private static final Pattern META_PATTERN = Pattern.compile(
            "대회일\\s*:\\s*(\\d{4}-\\d{2}-\\d{2})\\s*~\\s*(\\d{4}-\\d{2}-\\d{2}),\\s*"
                    + "접수일\\s*:\\s*\\d{4}-\\d{2}-\\d{2}\\s*~\\s*(\\d{4}-\\d{2}-\\d{2})(?:\\s+(\\d{2}:\\d{2}:\\d{2}))?");

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    // 상세 페이지 문서 → 대회 시작·종료일, 접수 마감일시. 패턴을 못 찾으면 ConcoursCrawlException
    public static ConcoursDetailInfo parse(Document document) {
        Element meta = document.selectFirst("meta[name=description]");
        String content = meta != null ? meta.attr("content") : "";

        Matcher matcher = META_PATTERN.matcher(content);
        if (!matcher.find()) {
            throw new ConcoursCrawlException("상세 페이지에서 대회일/접수일 정보를 찾을 수 없습니다: " + document.location());
        }

        LocalDate startDate = LocalDate.parse(matcher.group(1), DATE_FORMAT);
        LocalDate endDate = LocalDate.parse(matcher.group(2), DATE_FORMAT);
        LocalDate deadlineDate = LocalDate.parse(matcher.group(3), DATE_FORMAT);
        LocalTime deadlineTime = matcher.group(4) != null
                ? LocalTime.parse(matcher.group(4), TIME_FORMAT)
                : LocalTime.of(23, 59, 59);

        return new ConcoursDetailInfo(startDate.atStartOfDay(), endDate.atStartOfDay(), deadlineDate.atTime(deadlineTime));
    }
}
