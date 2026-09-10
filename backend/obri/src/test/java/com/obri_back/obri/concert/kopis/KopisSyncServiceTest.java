package com.obri_back.obri.concert.kopis;

import com.obri_back.obri.concert.entity.Concert;
import com.obri_back.obri.concert.repository.ConcertRepository;
import com.obri_back.obri.global.exception.ConflictException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KopisSyncServiceTest {

    @Mock KopisClient client;
    @Mock ConcertRepository concertRepository;
    @InjectMocks KopisSyncService kopisSyncService;

    private static final String ONE_ITEM_XML = """
            <dbs>
            <db>
            <mt20id>PF1</mt20id>
            <prfnm>신규 연주회</prfnm>
            <prfpdfrom>2026.12.11</prfpdfrom>
            <prfpdto>2026.12.11</prfpdto>
            <fcltynm>어느 콘서트홀</fcltynm>
            <poster>http://example.com/poster.gif</poster>
            <area>서울특별시</area>
            <genrenm>서양음악(클래식)</genrenm>
            </db>
            </dbs>
            """;

    private static final String EMPTY_XML = "<dbs></dbs>";

    private Document parseXml(String xml) {
        return Jsoup.parse(xml, "", Parser.xmlParser());
    }

    private Concert existingConcert() {
        return Concert.fromSync("PF1", "신규 연주회", "서양음악(클래식)",
                LocalDate.of(2026, 12, 11), LocalDate.of(2026, 12, 11),
                "어느 콘서트홀", "서울특별시", "http://example.com/poster.gif",
                "https://www.kopis.or.kr/por/db/pblprfr/pblprfrView.do?menuId=MNU_00020&mt20Id=PF1");
    }

    // CCCA(서양음악/클래식)에만 항목이 있고, CCCC(국악)·CCCD(대중음악)는 1페이지부터 비어있는 전형적인 상황을 재현
    private void stubOnlyClassicalGenreHasItem() {
        when(client.fetchListDocument(anyString(), anyString(), eq(1), eq(100), eq("CCCA")))
                .thenReturn(parseXml(ONE_ITEM_XML));
        when(client.fetchListDocument(anyString(), anyString(), eq(2), eq(100), eq("CCCA")))
                .thenReturn(parseXml(EMPTY_XML));
        when(client.fetchListDocument(anyString(), anyString(), eq(1), eq(100), eq("CCCC")))
                .thenReturn(parseXml(EMPTY_XML));
        when(client.fetchListDocument(anyString(), anyString(), eq(1), eq(100), eq("CCCD")))
                .thenReturn(parseXml(EMPTY_XML));
    }

    @Test
    void sync_savesNewItemAndStopsAtEmptyPagePerGenre() {
        stubOnlyClassicalGenreHasItem();
        when(concertRepository.findByExternalId("PF1")).thenReturn(Optional.empty());

        int savedCount = kopisSyncService.sync();

        assertThat(savedCount).isEqualTo(1);
        verify(concertRepository, times(1)).save(any(Concert.class));
        verify(client, never()).fetchListDocument(anyString(), anyString(), eq(3), eq(100), eq("CCCA"));
        verify(client, never()).fetchListDocument(anyString(), anyString(), eq(2), eq(100), eq("CCCC"));
        verify(client, never()).fetchListDocument(anyString(), anyString(), eq(2), eq(100), eq("CCCD"));
    }

    @Test
    void sync_updatesExistingItemWithoutCountingAsNew() {
        Concert existing = existingConcert();
        stubOnlyClassicalGenreHasItem();
        when(concertRepository.findByExternalId("PF1")).thenReturn(Optional.of(existing));

        int savedCount = kopisSyncService.sync();

        assertThat(savedCount).isZero(); // 갱신이지 신규 저장이 아니므로 신규 건수는 0
        verify(concertRepository, times(1)).save(existing);
    }

    @Test
    void sync_returnsZeroWhenEveryGenreFirstPageIsEmpty() {
        when(client.fetchListDocument(anyString(), anyString(), eq(1), eq(100), anyString()))
                .thenReturn(parseXml(EMPTY_XML));

        int savedCount = kopisSyncService.sync();

        assertThat(savedCount).isZero();
        verifyNoInteractions(concertRepository);
        verify(client, never()).fetchListDocument(anyString(), anyString(), eq(2), eq(100), anyString());
    }

    @Test
    void sync_treatsGenreFetchFailureAsEmptyPageAndContinuesOtherGenres() {
        when(client.fetchListDocument(anyString(), anyString(), eq(1), eq(100), anyString()))
                .thenThrow(new KopisSyncException("네트워크 오류"));

        int savedCount = kopisSyncService.sync();

        assertThat(savedCount).isZero();
        verifyNoInteractions(concertRepository);
        // 장르 3개(CCCA/CCCC/CCCD) 각각 1페이지는 시도해야 함
        verify(client, times(3)).fetchListDocument(anyString(), anyString(), eq(1), eq(100), anyString());
    }

    @Test
    void sync_throwsConflictWhenAlreadyRunning() throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        when(client.fetchListDocument(anyString(), anyString(), eq(1), eq(100), anyString())).thenAnswer(invocation -> {
            started.countDown();
            release.await();
            return parseXml(EMPTY_XML);
        });

        Thread firstRun = new Thread(kopisSyncService::sync);
        firstRun.start();

        assertThat(started.await(5, TimeUnit.SECONDS)).isTrue();
        assertThatThrownBy(() -> kopisSyncService.sync())
                .isInstanceOf(ConflictException.class);

        release.countDown();
        firstRun.join(5000);
    }
}
