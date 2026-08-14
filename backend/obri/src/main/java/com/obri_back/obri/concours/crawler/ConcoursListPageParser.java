package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.crawler.dto.ConcoursListItem;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * 콩쿠르 목록 페이지(table.boardlisttable) 파싱 — 행마다 상세 링크·제목·카테고리·주최만 추출
 * 순수 파싱 로직만 담당(네트워크 없음)이라 네트워크 없이 유닛테스트 가능
 */
public class ConcoursListPageParser {

    private static final Pattern DETAIL_URL_PATTERN = Pattern.compile("location\\.href='([^']+)'");

    // 목록 페이지 문서 → 행별 원시 항목 목록. 링크를 못 뽑는 행은 건너뜀(배너 등 이물질 방지)
    public static List<ConcoursListItem> parse(Document document) {
        Elements rows = document.select("table.boardlisttable tbody tr[onclick]");
        List<ConcoursListItem> items = new ArrayList<>();

        for (Element row : rows) {
            String detailUrl = extractDetailUrl(row.attr("onclick"));
            if (detailUrl == null) {
                continue;
            }

            String title = row.selectFirst("td.subject div.main_contitle") != null
                    ? row.selectFirst("td.subject div.main_contitle").text().trim()
                    : null;
            if (title == null || title.isEmpty()) {
                continue;
            }

            String category = extractCategory(row);
            String organizer = row.selectFirst("td.boardinfo") != null
                    ? row.selectFirst("td.boardinfo").text().trim()
                    : "";

            items.add(new ConcoursListItem(title, category, organizer, detailUrl));
        }

        return items;
    }

    // "추천"·"인기" 같은 프로모션 태그가 앞에 붙을 수 있어, 마지막 span.bigcate를 실제 카테고리로 간주
    private static String extractCategory(Element row) {
        Elements bigcateSpans = row.select("td.subject p.category span.bigcate");
        if (bigcateSpans.isEmpty()) {
            return "기타";
        }
        return bigcateSpans.last().text().trim();
    }

    private static String extractDetailUrl(String onclickAttr) {
        if (onclickAttr == null) {
            return null;
        }
        Matcher matcher = DETAIL_URL_PATTERN.matcher(onclickAttr);
        return matcher.find() ? matcher.group(1) : null;
    }
}
