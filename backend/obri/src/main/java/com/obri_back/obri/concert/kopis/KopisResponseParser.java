package com.obri_back.obri.concert.kopis;

import com.obri_back.obri.concert.kopis.dto.KopisPerformanceItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
 * KOPIS 공연목록 XML(<dbs><db>...</db></dbs>) 파싱 — 순수 파싱 로직만 담당(네트워크 없음)이라
 * 네트워크 없이 유닛테스트 가능
 */
public class KopisResponseParser {

    private static final DateTimeFormatter KOPIS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");
    // KOPIS 오픈API 응답엔 상세 페이지 링크가 없어 mt20id로 직접 조립
    private static final String DETAIL_URL_TEMPLATE =
            "https://www.kopis.or.kr/por/db/pblprfr/pblprfrView.do?menuId=MNU_00020&mt20Id=%s";

    // 목록 응답 문서 → 항목 목록. <db> 태그가 하나도 없으면(빈 <dbs/>) 빈 리스트 반환 — 마지막 페이지 판단 기준
    public static List<KopisPerformanceItem> parse(Document document) {
        Elements rows = document.select("db");
        List<KopisPerformanceItem> items = new ArrayList<>();

        for (Element row : rows) {
            String externalId = text(row, "mt20id");
            if (externalId == null || externalId.isEmpty()) {
                continue; // 식별자 없는 행은 upsert 기준이 없어 스킵
            }

            items.add(new KopisPerformanceItem(
                    externalId,
                    text(row, "prfnm"),
                    text(row, "genrenm"),
                    parseDate(text(row, "prfpdfrom")),
                    parseDate(text(row, "prfpdto")),
                    text(row, "fcltynm"),
                    text(row, "area"),
                    text(row, "poster"),
                    String.format(DETAIL_URL_TEMPLATE, externalId)
            ));
        }

        return items;
    }

    private static String text(Element row, String tag) {
        Element el = row.selectFirst(tag);
        return el != null ? el.text().trim() : null;
    }

    private static LocalDate parseDate(String raw) {
        return raw == null || raw.isEmpty() ? null : LocalDate.parse(raw, KOPIS_DATE_FORMAT);
    }
}
