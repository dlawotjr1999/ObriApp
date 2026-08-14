package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.crawler.dto.ConcoursListItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConcoursListPageParserTest {

    private static final String TABLE_HTML = """
            <table class="boardlisttable">
              <tbody>
                <tr onclick="location.href='https://contest.co.kr/contest/view1/22440'">
                  <td class="subject">
                    <p class="category"><span class="bigcate">추천</span><span style="border:1px solid #1a9585;">서울</span><span class="bigcate">클래식/실용</span></p>
                    <div class="main_contitle">예화입시평가회</div>
                  </td>
                  <td class="boardinfo">서울음악</td>
                  <td class="day">D-155</td>
                  <td class="stat"><span>접수중</span></td>
                </tr>
                <tr onclick="location.href='https://contest.co.kr/contest/view1/22282'">
                  <td class="subject">
                    <p class="category"><span style="border:1px solid #1a9585;">서울</span><span class="bigcate">클래식/실용</span></p>
                    <div class="main_contitle">2027 안톤 피아노·현악 입시평가회</div>
                  </td>
                  <td class="boardinfo">안톤홀</td>
                  <td class="day">D-148</td>
                  <td class="stat"><span>접수중</span></td>
                </tr>
                <tr>
                  <td class="subject">
                    <div class="main_contitle">onclick 없는 광고성 행 — 건너뛰어야 함</div>
                  </td>
                </tr>
              </tbody>
            </table>
            """;

    @Test
    void parse_extractsAllWellFormedRows() {
        Document document = Jsoup.parse(TABLE_HTML);

        List<ConcoursListItem> items = ConcoursListPageParser.parse(document);

        assertThat(items).hasSize(2);
    }

    @Test
    void parse_takesLastBigcateAsCategoryWhenPromoTagPresent() {
        Document document = Jsoup.parse(TABLE_HTML);

        List<ConcoursListItem> items = ConcoursListPageParser.parse(document);

        assertThat(items.get(0).title()).isEqualTo("예화입시평가회");
        assertThat(items.get(0).category()).isEqualTo("클래식/실용");
        assertThat(items.get(0).organizer()).isEqualTo("서울음악");
        assertThat(items.get(0).detailUrl()).isEqualTo("https://contest.co.kr/contest/view1/22440");
    }

    @Test
    void parse_usesOnlyBigcateWhenNoPromoTag() {
        Document document = Jsoup.parse(TABLE_HTML);

        List<ConcoursListItem> items = ConcoursListPageParser.parse(document);

        assertThat(items.get(1).category()).isEqualTo("클래식/실용");
    }

    @Test
    void parse_skipsRowWithoutOnclick() {
        Document document = Jsoup.parse(TABLE_HTML);

        List<ConcoursListItem> items = ConcoursListPageParser.parse(document);

        assertThat(items).noneMatch(item -> item.title().contains("광고성"));
    }

    @Test
    void parse_returnsEmptyListWhenNoTable() {
        Document document = Jsoup.parse("<html><body>no table here</body></html>");

        List<ConcoursListItem> items = ConcoursListPageParser.parse(document);

        assertThat(items).isEmpty();
    }
}
