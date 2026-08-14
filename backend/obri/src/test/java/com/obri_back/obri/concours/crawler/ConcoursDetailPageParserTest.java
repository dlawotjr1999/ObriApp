package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.crawler.dto.ConcoursDetailInfo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConcoursDetailPageParserTest {

    @Test
    void parse_extractsDatesFromMetaDescription() {
        String html = """
                <html><head>
                <meta name="description" content="대회일 : 2026-06-14 ~ 2027-01-09, 접수일 : 2026-07-30 ~ 2027-01-09 23:59:59" />
                </head><body></body></html>
                """;
        Document document = Jsoup.parse(html);

        ConcoursDetailInfo info = ConcoursDetailPageParser.parse(document);

        assertThat(info.startDate()).isEqualTo(LocalDateTime.of(2026, 6, 14, 0, 0));
        assertThat(info.endDate()).isEqualTo(LocalDateTime.of(2027, 1, 9, 0, 0));
        assertThat(info.deadline()).isEqualTo(LocalDateTime.of(2027, 1, 9, 23, 59, 59));
    }

    @Test
    void parse_defaultsToEndOfDayWhenDeadlineTimeMissing() {
        String html = """
                <html><head>
                <meta name="description" content="대회일 : 2026-06-14 ~ 2027-01-09, 접수일 : 2026-07-30 ~ 2027-01-09" />
                </head><body></body></html>
                """;
        Document document = Jsoup.parse(html);

        ConcoursDetailInfo info = ConcoursDetailPageParser.parse(document);

        assertThat(info.deadline().toLocalDate()).isEqualTo(LocalDateTime.of(2027, 1, 9, 0, 0).toLocalDate());
    }

    @Test
    void parse_throwsWhenMetaDescriptionMissing() {
        Document document = Jsoup.parse("<html><head></head><body>no meta</body></html>");

        assertThatThrownBy(() -> ConcoursDetailPageParser.parse(document))
                .isInstanceOf(ConcoursCrawlException.class);
    }

    @Test
    void parse_throwsWhenMetaDescriptionFormatUnexpected() {
        String html = """
                <html><head>
                <meta name="description" content="사이트 마크업이 바뀐 경우" />
                </head><body></body></html>
                """;
        Document document = Jsoup.parse(html);

        assertThatThrownBy(() -> ConcoursDetailPageParser.parse(document))
                .isInstanceOf(ConcoursCrawlException.class);
    }
}
