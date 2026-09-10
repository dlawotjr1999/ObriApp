package com.obri_back.obri.concert.kopis;

import com.obri_back.obri.concert.kopis.dto.KopisPerformanceItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KopisResponseParserTest {

    // 실제 KOPIS 오픈API(shcate=CCCA) 응답 샘플 축약본(2건)
    private static final String LIST_XML = """
            <dbs>
            <db>
            <mt20id>PF300640</mt20id>
            <prfnm>고음악 오디세이, 음악의 언어: 극장 (Theatrum)</prfnm>
            <prfpdfrom>2026.12.11</prfpdfrom>
            <prfpdto>2026.12.11</prfpdto>
            <fcltynm>반포심산아트홀</fcltynm>
            <poster>http://www.kopis.or.kr/upload/pfmPoster/PF_PF300640_260910_151118.gif</poster>
            <area>서울특별시</area>
            <genrenm>서양음악(클래식)</genrenm>
            <openrun>N</openrun>
            <prfstate>공연예정</prfstate>
            </db>
            <db>
            <mt20id>PF300632</mt20id>
            <prfnm>제92회 코리아나 챔버 뮤직 소사이어티 정기연주회</prfnm>
            <prfpdfrom>2026.10.11</prfpdfrom>
            <prfpdto>2026.10.11</prfpdto>
            <fcltynm>예술의전당 [서울]</fcltynm>
            <poster>http://www.kopis.or.kr/upload/pfmPoster/PF_PF300632_260910_144546.jpg</poster>
            <area>서울특별시</area>
            <genrenm>서양음악(클래식)</genrenm>
            <openrun>N</openrun>
            <prfstate>공연예정</prfstate>
            </db>
            </dbs>
            """;

    private static final String EMPTY_XML = "<dbs></dbs>";

    private static final String MISSING_ID_XML = """
            <dbs>
            <db>
            <prfnm>식별자 없는 항목 — 스킵돼야 함</prfnm>
            </db>
            </dbs>
            """;

    private Document parseXml(String xml) {
        return Jsoup.parse(xml, "", Parser.xmlParser());
    }

    @Test
    void parse_extractsAllItemsWithFields() {
        List<KopisPerformanceItem> items = KopisResponseParser.parse(parseXml(LIST_XML));

        assertThat(items).hasSize(2);

        KopisPerformanceItem first = items.get(0);
        assertThat(first.externalId()).isEqualTo("PF300640");
        assertThat(first.title()).isEqualTo("고음악 오디세이, 음악의 언어: 극장 (Theatrum)");
        assertThat(first.category()).isEqualTo("서양음악(클래식)");
        assertThat(first.startDate()).isEqualTo(LocalDate.of(2026, 12, 11));
        assertThat(first.endDate()).isEqualTo(LocalDate.of(2026, 12, 11));
        assertThat(first.venue()).isEqualTo("반포심산아트홀");
        assertThat(first.region()).isEqualTo("서울특별시");
        assertThat(first.posterUrl()).isEqualTo("http://www.kopis.or.kr/upload/pfmPoster/PF_PF300640_260910_151118.gif");
        assertThat(first.url()).isEqualTo(
                "https://www.kopis.or.kr/por/db/pblprfr/pblprfrView.do?menuId=MNU_00020&mt20Id=PF300640");
    }

    @Test
    void parse_returnsEmptyListWhenNoDbElements() {
        List<KopisPerformanceItem> items = KopisResponseParser.parse(parseXml(EMPTY_XML));

        assertThat(items).isEmpty();
    }

    @Test
    void parse_skipsItemWithoutExternalId() {
        List<KopisPerformanceItem> items = KopisResponseParser.parse(parseXml(MISSING_ID_XML));

        assertThat(items).isEmpty();
    }
}
