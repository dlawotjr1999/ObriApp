package com.obri_back.obri.concert.kopis;

import com.obri_back.obri.concert.entity.Concert;
import com.obri_back.obri.concert.kopis.dto.KopisPerformanceItem;
import com.obri_back.obri.concert.repository.ConcertRepository;
import com.obri_back.obri.global.exception.ConflictException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/*
 * KOPIS 동기화 오케스트레이션 — 오늘부터 SYNC_MONTHS_AHEAD개월 이내, 음악 계열 장르(GENRE_CODES)의
 * 공연을 장르별·페이지 단위로 가져와 신규 저장·기존 갱신을 수행한다. 목록 응답만으로 Concert 저장에
 * 필요한 필드가 전부 채워져(콩쿠르 크롤러와 달리) 상세 조회가 없고, 빈 페이지를 만나면 그 장르는 끝난
 * 것으로 보고 다음 장르로 넘어간다.
 * KOPIS API가 장르(shcate) 다중값을 지원하지 않아 장르 하나당 별도로 전체 페이지를 순회한다.
 * 스케줄러·수동 트리거가 공유하는 진입점이라 동시 실행은 AtomicBoolean으로 차단(ConcoursCrawlerService와 동일 패턴)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KopisSyncService {

    private static final DateTimeFormatter KOPIS_QUERY_DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int SYNC_MONTHS_AHEAD = 6;
    private static final int ROWS_PER_PAGE = 100;
    // 안전판 — 6개월치 공연이 장르당 5000건(50페이지 x 100건)을 넘길 일은 없다고 봄
    private static final int HARD_CAP_PAGE = 50;
    // 악기 연주와 관련 있는 음악 계열 장르만 대상. CCCA(서양음악/클래식)는 실제 호출로 확인했지만
    // CCCC(국악)·CCCD(대중음악)는 KOPIS 공통코드 추정값 — 검증 전까지 실제 응답으로 재확인 필요
    private static final List<String> GENRE_CODES = List.of("CCCA", "CCCC", "CCCD");

    private final KopisClient client;
    private final ConcertRepository concertRepository;

    private final AtomicBoolean running = new AtomicBoolean(false);

    // 동기화 1회 실행 — 저장된 신규 건수 반환
    public int sync() {
        if (!running.compareAndSet(false, true)) {
            throw new ConflictException("이미 KOPIS 동기화가 진행 중입니다");
        }
        try {
            return syncAllGenres();
        } finally {
            running.set(false);
        }
    }

    private int syncAllGenres() {
        String stdate = LocalDate.now().format(KOPIS_QUERY_DATE);
        String eddate = LocalDate.now().plusMonths(SYNC_MONTHS_AHEAD).format(KOPIS_QUERY_DATE);

        int newCount = 0;
        int updatedCount = 0;

        for (String genreCode : GENRE_CODES) {
            GenreSyncResult result = syncGenrePages(stdate, eddate, genreCode);
            newCount += result.newCount();
            updatedCount += result.updatedCount();
        }

        log.info("KOPIS 동기화 완료 — 신규 {}건 저장, 기존 {}건 갱신", newCount, updatedCount);
        return newCount;
    }

    private record GenreSyncResult(int newCount, int updatedCount) {
    }

    private GenreSyncResult syncGenrePages(String stdate, String eddate, String genreCode) {
        int newCount = 0;
        int updatedCount = 0;

        for (int page = 1; page <= HARD_CAP_PAGE; page++) {
            List<KopisPerformanceItem> items = fetchPageSafely(stdate, eddate, page, genreCode);
            if (items == null || items.isEmpty()) {
                break; // 빈 페이지(또는 조회 실패) — 이 장르는 여기서 중단, 다음 스케줄 회차에서 처음부터 재시도
            }

            for (KopisPerformanceItem item : items) {
                if (saveOrUpdate(item)) {
                    newCount++;
                } else {
                    updatedCount++;
                }
            }
        }

        return new GenreSyncResult(newCount, updatedCount);
    }

    private List<KopisPerformanceItem> fetchPageSafely(String stdate, String eddate, int page, String genreCode) {
        try {
            Document document = client.fetchListDocument(stdate, eddate, page, ROWS_PER_PAGE, genreCode);
            return KopisResponseParser.parse(document);
        } catch (KopisSyncException e) {
            log.warn("KOPIS 목록 페이지 조회 실패: genre={}, page={}", genreCode, page, e);
            return null;
        }
    }

    // true = 신규 저장, false = 기존 갱신
    private boolean saveOrUpdate(KopisPerformanceItem item) {
        Optional<Concert> existing = concertRepository.findByExternalId(item.externalId());
        if (existing.isPresent()) {
            Concert concert = existing.get();
            concert.updateFromSync(item.title(), item.category(), item.startDate(), item.endDate(),
                    item.venue(), item.region(), item.posterUrl(), item.url());
            concertRepository.save(concert);
            return false;
        }

        concertRepository.save(Concert.fromSync(item.externalId(), item.title(), item.category(),
                item.startDate(), item.endDate(), item.venue(), item.region(), item.posterUrl(), item.url()));
        return true;
    }
}
