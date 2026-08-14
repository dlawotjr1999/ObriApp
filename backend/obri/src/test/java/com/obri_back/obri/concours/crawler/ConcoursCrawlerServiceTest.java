package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.entity.Concours;
import com.obri_back.obri.concours.repository.ConcoursRepository;
import com.obri_back.obri.global.exception.ConflictException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConcoursCrawlerServiceTest {

    @Mock ConcoursCrawlerClient client;
    @Mock ConcoursRepository concoursRepository;
    @InjectMocks ConcoursCrawlerService concoursCrawlerService;

    private static final String ONE_ROW_TABLE = """
            <table class="boardlisttable"><tbody>
              <tr onclick="location.href='https://contest.co.kr/contest/view1/1'">
                <td class="subject">
                  <p class="category"><span class="bigcate">클래식/실용</span></p>
                  <div class="main_contitle">신규 콩쿠르</div>
                </td>
                <td class="boardinfo">주최사</td>
              </tr>
            </tbody></table>
            """;

    private static final String TWO_ROW_TABLE = """
            <table class="boardlisttable"><tbody>
              <tr onclick="location.href='https://contest.co.kr/contest/view1/1'">
                <td class="subject">
                  <p class="category"><span class="bigcate">클래식/실용</span></p>
                  <div class="main_contitle">중복 콩쿠르 1</div>
                </td>
                <td class="boardinfo">주최사</td>
              </tr>
              <tr onclick="location.href='https://contest.co.kr/contest/view1/2'">
                <td class="subject">
                  <p class="category"><span class="bigcate">클래식/실용</span></p>
                  <div class="main_contitle">중복 콩쿠르 2</div>
                </td>
                <td class="boardinfo">주최사</td>
              </tr>
            </tbody></table>
            """;

    private static final String DETAIL_HTML = """
            <html><head>
            <meta name="description" content="대회일 : 2026-06-14 ~ 2027-01-09, 접수일 : 2026-07-30 ~ 2027-01-09 23:59:59" />
            </head><body></body></html>
            """;

    private static final String EMPTY_TABLE = "<table class=\"boardlisttable\"><tbody></tbody></table>";

    @Test
    void crawl_stopsWhenFirstPageIsAllDuplicates() {
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(TWO_ROW_TABLE));
        when(concoursRepository.existsByTitleAndUrl(anyString(), anyString())).thenReturn(true);

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isZero();
        verify(client, never()).fetchListDocument(2);
        verify(client, never()).fetchDetailDocument(anyString());
    }

    @Test
    void crawl_savesNewItemAndStopsOnEmptyNextPage() {
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(ONE_ROW_TABLE));
        when(client.fetchListDocument(2)).thenReturn(Jsoup.parse(EMPTY_TABLE));
        when(concoursRepository.existsByTitleAndUrl(eq("신규 콩쿠르"), anyString())).thenReturn(false);
        when(client.fetchDetailDocument("https://contest.co.kr/contest/view1/1")).thenReturn(Jsoup.parse(DETAIL_HTML));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isEqualTo(1);
        verify(concoursRepository, times(1)).save(any(Concours.class));
        verify(client, never()).fetchListDocument(3);
    }

    @Test
    void crawl_skipsItemWhenDetailPageMissingExpectedFormat() {
        // 상세 파싱 실패도 newInPage=0으로 취급돼 같은 페이지에서 멈추므로 2페이지는 호출되지 않음
        String malformedDetail = "<html><head></head><body>no meta description</body></html>";
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(ONE_ROW_TABLE));
        when(concoursRepository.existsByTitleAndUrl(anyString(), anyString())).thenReturn(false);
        when(client.fetchDetailDocument(anyString())).thenReturn(Jsoup.parse(malformedDetail));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isZero();
        verify(concoursRepository, never()).save(any(Concours.class));
        verify(client, never()).fetchListDocument(2);
    }

    @Test
    void crawl_throwsConflictWhenAlreadyRunning() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        when(client.fetchListDocument(1)).thenAnswer(invocation -> {
            started.countDown();
            release.await();
            return Jsoup.parse(EMPTY_TABLE);
        });

        Thread firstRun = new Thread(concoursCrawlerService::crawl);
        firstRun.start();

        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        assertThatThrownBy(() -> concoursCrawlerService.crawl())
                .isInstanceOf(ConflictException.class);

        release.countDown();
        firstRun.join(5000);
    }
}
