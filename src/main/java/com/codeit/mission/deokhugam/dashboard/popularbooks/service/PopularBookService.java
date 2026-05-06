package com.codeit.mission.deokhugam.dashboard.popularbooks.service;

import com.codeit.mission.deokhugam.book.exception.IllegalLimitException;
import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import com.codeit.mission.deokhugam.dashboard.dto.request.ParsedCursors;
import com.codeit.mission.deokhugam.dashboard.dto.request.StringCursors;
import com.codeit.mission.deokhugam.dashboard.exceptions.CursorAfterNotProvidedTogetherException;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.repository.PopularBookRepository;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshot;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotRepository;
import com.codeit.mission.deokhugam.dashboard.util.Utils;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopularBookService {

  private static final int MAX_PAGE_SIZE = 100; // 페이지의 최대 사이즈는 100으로 고정

  private final PopularBookRepository popularBookRepository;
  private final AggregateSnapshotRepository aggregateSnapshotRepository;

  // 인기 도서를 조회하는 서비스 계층 메서드
  // 조회 캐시 어노테이션을 추가
  // 메서드 본문을 실행하기 전 캐시를 먼저 확인한다.
  @Cacheable(
      cacheNames = "popularBooks", // 사용할 캐시의 이름은 popularBooks (RedisConfig에서 설정함)

      // Redis Cache Key는 해당 형태로 지정함.
      key = "'period=' + #periodType"
          + " + ':direction=' + #direction"
          + " + ':cursor=' + (#cursor == null ? 'null' : #cursor)"
          + " + ':after=' + (#after == null ? 'null' : #after)"
          + " + ':size=' + #pageSize"
  )

  @Transactional(readOnly = true)
  public CursorPageResponsePopularBookDto get(
      PeriodType periodType, DirectionEnum direction, String cursor, String after, int pageSize
  ) {
    log.info("[POPULAR_BOOK_QUERY_START] periodType={}, direction={}, cursor={}, after={}, pageSize={}",
        periodType, direction, cursor, after, pageSize);

    // 커서와 보조 커서는 항상 같이 제공되어야 합니다.
    if ((cursor == null) != (after == null)) {
      throw new CursorAfterNotProvidedTogetherException();
    }

    // 페이지 사이즈가 0 이하거나 최대 페이지 사이즈를 넘어가면 커스텀 예외를 발행
    if (pageSize > MAX_PAGE_SIZE || pageSize <= 0) {
      throw new IllegalLimitException();
    }

    if (direction == null || periodType == null) {
      throw new IllegalArgumentException("periodType과 Direction은 무조건 필수입니다!");
    }

    // 커서가 존재하면 String으로 부터 Long,LocalDateTime으로 파싱
    // 존재하지 않다면 둘 다 null로 초기화
    ParsedCursors cursors = Utils.parseCursors(cursor, after);
    Long cursorLong = cursors.cursor();
    Instant afterDate = cursors.after();

    // 찾고자하는 기간에 해당하는 스냅샷의 ID를 구합니다.
    Optional<UUID> publishedSnapshotId = getPublishedSnapshotId(periodType);
    if (publishedSnapshotId.isEmpty()) {
      log.info("[POPULAR_BOOK_QUERY_EMPTY] periodType={}, reason=published_snapshot_not_found",
          periodType);
      return new CursorPageResponsePopularBookDto(
          Collections.emptyList(),
          null,
          null,
          pageSize,
          0L,
          false);
    }

    // 커서 페이지 응답에 content 안에 들어갈 PopularBookDto 리스트
    List<PopularBookDto> rows = (direction == DirectionEnum.ASC)
        ? popularBookRepository.findRankingDtosBySnapshotIdAsc(publishedSnapshotId.get(),
        cursorLong, afterDate, PageRequest.of(0, pageSize + 1))
        : popularBookRepository.findRankingDtosBySnapshotIdDesc(publishedSnapshotId.get(),
            cursorLong, afterDate, PageRequest.of(0, pageSize + 1));

    // 뽑아온 rows의 사이즈가 한 페이지의 사이즈보다 크면 다음 페이지가 존재합니다.
    boolean hasNext = rows.size() > pageSize;
    // 다음 페이지가 존재하면 rows에서 페이지 사이즈 만큼 뽑아오고,
    // 존재하지 않으면 rows를 통째로 뽑습니다.
    List<PopularBookDto> content = hasNext ? new ArrayList<>(rows.subList(0, pageSize)) : rows;

    // 다음 페이지를 조회하기 위한 Cursor와 보조 커서를 구함
    // 다음 페이지가 존재하지 않으면 다음 커서 / 보조 커서는 null 처리
    StringCursors nextCursors = getNextCursors(hasNext, content);
    String nextCursor = nextCursors.cursor();
    String nextAfter = nextCursors.after();

    // 스냅샷에 해당하는 요소들의 총 개수를 센다.
    long totalElements = popularBookRepository.countRankingsBySnapshotId(publishedSnapshotId.get());
    log.info(
        "[POPULAR_BOOK_QUERY_DONE] periodType={}, snapshotId={}, contentSize={}, totalElements={}, hasNext={}",
        periodType, publishedSnapshotId.get(), content.size(), totalElements, hasNext);

    return new CursorPageResponsePopularBookDto(
        content,
        nextCursor,
        nextAfter,
        pageSize,
        totalElements,
        hasNext);
  }

  // periodType과 DomainType=인기 도서로 snapshot ID를 뽑아옵니다.
  private Optional<UUID> getPublishedSnapshotId(PeriodType periodType) {
    return aggregateSnapshotRepository
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, periodType, StagingType.PUBLISHED)
        .map(AggregateSnapshot::getSnapshotId);
  }

  private StringCursors getNextCursors(boolean hasNext, List<PopularBookDto> content) {
    if (hasNext && !content.isEmpty()) {
      PopularBookDto last = content.get(content.size() - 1);
      return new StringCursors(String.valueOf(last.rank()), String.valueOf(last.createdAt()));
    }
    return new StringCursors(null, null);
  }

}
