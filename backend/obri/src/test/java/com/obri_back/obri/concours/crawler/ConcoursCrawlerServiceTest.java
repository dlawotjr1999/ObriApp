package com.obri_back.obri.concours.crawler;

import com.obri_back.obri.concours.entity.Concours;
import com.obri_back.obri.concours.repository.ConcoursRepository;
import com.obri_back.obri.global.exception.BadRequestException;
import com.obri_back.obri.global.exception.ConflictException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
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

    // 페이지네이션 링크가 없는 단일 페이지 fixture 목록 페이지는 totalPages=1로 파싱됨
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

    // 1페이지 항목은 전부 중복이지만, pg=2 페이지네이션 링크가 있어 totalPages=2로 파싱됨
    private static final String TWO_ROW_TABLE_WITH_NEXT_PAGE_LINK = TWO_ROW_TABLE + """
            <div class="pager"><a href='https://contest.co.kr/contest/list_n/1?s_area=&t=2&list_state=&limit_=1&ncount=50&pg=2'>2</a></div>
            """;

    private static final String PAGE2_NEW_ITEM_TABLE = """
            <table class="boardlisttable"><tbody>
              <tr onclick="location.href='https://contest.co.kr/contest/view1/3'">
                <td class="subject">
                  <p class="category"><span class="bigcate">클래식/실용</span></p>
                  <div class="main_contitle">2페이지 신규 콩쿠르</div>
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

    // 마감이 이미 지난 기존 항목 fixture — 재크롤링 시 상세 재조회 없이 스킵되어야 함
    private Concours pastDeadlineConcours(String title, String url) {
        LocalDateTime past = LocalDateTime.now().minusDays(1);
        return Concours.fromCrawl(title, "클래식/실용", "주최사", url, past.minusMonths(1), past, past);
    }

    @Test
    void crawl_skipsExistingItemPastDeadlineWithoutRefetchingDetail() {
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(TWO_ROW_TABLE));
        when(concoursRepository.findByTitleAndUrl(anyString(), anyString()))
                .thenReturn(Optional.of(pastDeadlineConcours("중복 콩쿠르 1", "https://contest.co.kr/contest/view1/1")));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isZero();
        verify(client, never()).fetchListDocument(2);
        verify(client, never()).fetchDetailDocument(anyString());
        verify(concoursRepository, never()).save(any(Concours.class));
    }

    @Test
    void crawl_updatesExistingItemBeforeDeadlineWithFreshDates() {
        Concours activeItem = Concours.fromCrawl("신규 콩쿠르", "클래식/실용", "주최사",
                "https://contest.co.kr/contest/view1/1",
                LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusMonths(1), LocalDateTime.now().plusDays(5));
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(ONE_ROW_TABLE));
        when(concoursRepository.findByTitleAndUrl(eq("신규 콩쿠르"), anyString())).thenReturn(Optional.of(activeItem));
        when(client.fetchDetailDocument("https://contest.co.kr/contest/view1/1")).thenReturn(Jsoup.parse(DETAIL_HTML));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isZero(); // 갱신이지 신규 저장이 아니므로 신규 건수는 0
        assertThat(activeItem.getDeadline()).isEqualTo(LocalDateTime.of(2027, 1, 9, 23, 59, 59));
        verify(concoursRepository, times(1)).save(activeItem);
    }

    @Test
    void crawl_savesNewItemFromSinglePage() {
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(ONE_ROW_TABLE));
        when(concoursRepository.findByTitleAndUrl(eq("신규 콩쿠르"), anyString())).thenReturn(Optional.empty());
        when(client.fetchDetailDocument("https://contest.co.kr/contest/view1/1")).thenReturn(Jsoup.parse(DETAIL_HTML));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isEqualTo(1);
        verify(concoursRepository, times(1)).save(any(Concours.class));
        verify(client, never()).fetchListDocument(2);
    }

    @Test
    void crawl_continuesToNextPageEvenWhenPreviousPageAllDuplicate() {
        // BACKLOG #42 회귀 테스트 — 1페이지가 전부 중복이어도 페이지네이션 링크가 있으면 2페이지까지 순회해야 함
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(TWO_ROW_TABLE_WITH_NEXT_PAGE_LINK));
        when(client.fetchListDocument(2)).thenReturn(Jsoup.parse(PAGE2_NEW_ITEM_TABLE));
        when(concoursRepository.findByTitleAndUrl(eq("중복 콩쿠르 1"), anyString()))
                .thenReturn(Optional.of(pastDeadlineConcours("중복 콩쿠르 1", "https://contest.co.kr/contest/view1/1")));
        when(concoursRepository.findByTitleAndUrl(eq("중복 콩쿠르 2"), anyString()))
                .thenReturn(Optional.of(pastDeadlineConcours("중복 콩쿠르 2", "https://contest.co.kr/contest/view1/2")));
        when(concoursRepository.findByTitleAndUrl(eq("2페이지 신규 콩쿠르"), anyString())).thenReturn(Optional.empty());
        when(client.fetchDetailDocument("https://contest.co.kr/contest/view1/3")).thenReturn(Jsoup.parse(DETAIL_HTML));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isEqualTo(1);
        verify(client, times(1)).fetchListDocument(2);
        verify(concoursRepository, times(1)).save(any(Concours.class));
    }

    @Test
    void crawl_skipsItemWhenDetailPageMissingExpectedFormat() {
        String malformedDetail = "<html><head></head><body>no meta description</body></html>";
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(ONE_ROW_TABLE));
        when(concoursRepository.findByTitleAndUrl(anyString(), anyString())).thenReturn(Optional.empty());
        when(client.fetchDetailDocument(anyString())).thenReturn(Jsoup.parse(malformedDetail));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isZero();
        verify(concoursRepository, never()).save(any(Concours.class));
    }

    @Test
    void crawl_returnsZeroWhenFirstPageFetchFails() {
        when(client.fetchListDocument(1)).thenThrow(new ConcoursCrawlException("네트워크 오류"));

        int savedCount = concoursCrawlerService.crawl();

        assertThat(savedCount).isZero();
        verifyNoInteractions(concoursRepository);
    }

    @Test
    void crawl_stopsAtMaxPagesWhenProvided() {
        // 전체는 2페이지(pg=2 링크 존재)지만 maxPages=1이면 1페이지만 순회해야 함
        when(client.fetchListDocument(1)).thenReturn(Jsoup.parse(TWO_ROW_TABLE_WITH_NEXT_PAGE_LINK));
        when(concoursRepository.findByTitleAndUrl(anyString(), anyString()))
                .thenReturn(Optional.of(pastDeadlineConcours("중복 콩쿠르 1", "https://contest.co.kr/contest/view1/1")));

        int savedCount = concoursCrawlerService.crawl(1);

        assertThat(savedCount).isZero();
        verify(client, never()).fetchListDocument(2);
    }

    @Test
    void crawl_throwsBadRequestWhenMaxPagesNotPositive() {
        assertThatThrownBy(() -> concoursCrawlerService.crawl(0))
                .isInstanceOf(BadRequestException.class);

        verifyNoInteractions(client, concoursRepository);
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
