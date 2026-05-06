package com.codeit.mission.deokhugam.dashboard.popularreviews.service;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import com.codeit.mission.deokhugam.dashboard.exceptions.CursorAfterNotProvidedTogetherException;
import com.codeit.mission.deokhugam.dashboard.exceptions.InvalidCursorValueException;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response.CursorPageResponsePopularReviewDto;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response.PopularReviewDto;
import com.codeit.mission.deokhugam.dashboard.popularreviews.repository.PopularReviewRepository;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshot;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotRepository;
import java.time.DateTimeException;
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
public class PopularReviewService {

  private static final int MAX_PAGE_SIZE = 100;

  private final PopularReviewRepository popularReviewRepository;
  private final AggregateSnapshotRepository aggregateSnapshotRepository;

  // 인기 리뷰를 조회하는 서비스 메서드
  @Cacheable(
      cacheNames = "popularReviews",
      key = "'period=' + #periodType"
          + " + ':direction=' + #direction"
          + " + ':cursor=' + (#cursor == null ? 'null' : #cursor)"
          + " + ':after=' + (#after == null ? 'null' : #after)"
          + " + ':size=' + #size"
  )
  @Transactional(readOnly = true)
  public CursorPageResponsePopularReviewDto getReviews(
      PeriodType periodType, DirectionEnum direction, String cursor, String after, int size) {
    log.info("[POPULAR_REVIEW_QUERY_START] periodType={}, direction={}, cursor={}, after={}, size={}",
        periodType, direction, cursor, after, size);

    // 커서와 보조 커서는 항상 같이 제공되어야 합니다.
    if ((cursor == null) != (after == null)) {
      throw new CursorAfterNotProvidedTogetherException();
    }

    // 페이지 사이즈는 1 ~ 100 범위를 벗어나지 않음.
    int pageSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);

    // 커서가 존재하면 String으로 부터 Long,LocalDate로 파싱
    // 존재하지 않다면 둘 다 null로 초기화
    ParsedCursors cursors = parseCursors(cursor, after);
    Long cursorLong = cursors.cursor();
    Instant afterDate = cursors.after();

    // 찾고자하는 기간에 해당하는 스냅샷의 ID를 구합니다.
    Optional<UUID> publishedSnapshotId = getPublishedSnapshotId(periodType);
    if (publishedSnapshotId.isEmpty()) {
      log.info("[POPULAR_REVIEW_QUERY_EMPTY] periodType={}, reason=published_snapshot_not_found",
          periodType);
      return new CursorPageResponsePopularReviewDto(
          Collections.emptyList(),
          null,
          null,
          pageSize,
          0L,
          false);
    }

    // 커서 페이지 응답에 content 안에 들어갈 PopularReviewDto 리스트
    List<PopularReviewDto> rows = (direction == DirectionEnum.ASC)
        ? popularReviewRepository.findRankingDtosBySnapshotIdAsc(publishedSnapshotId.get(),
        cursorLong, afterDate, PageRequest.of(0, pageSize + 1))
        : popularReviewRepository.findRankingDtosBySnapshotIdDesc(publishedSnapshotId.get(),
            cursorLong, afterDate, PageRequest.of(0, pageSize + 1));

    // 뽑아온 rows의 사이즈가 한 페이지의 사이즈보다 크면 다음 페이지가 존재합니다.
    boolean hasNext = rows.size() > pageSize;
    // 다음 페이지가 존재하면 rows에서 페이지 사이즈 만큼 뽑아오고,
    // 존재하지 않으면 rows를 통째로 뽑습니다.
    List<PopularReviewDto> content = hasNext ? new ArrayList<>(rows.subList(0, pageSize)) : rows;

    // 다음 페이지를 조회하기 위한 Cursor와 보조 커서를 구함
    // 다음 페이지가 존재하지 않으면 다음 커서 / 보조 커서는 null 처리
    StringCursors nextCursors = getNextCursors(hasNext, content);
    String nextCursor = nextCursors.cursor();
    String nextAfter = nextCursors.after();

    // 스냅샷에 해당하는 요소들의 총 개수를 센다.
    long totalElements = popularReviewRepository.countRankingsBySnapshotId(
        publishedSnapshotId.get());
    log.info(
        "[POPULAR_REVIEW_QUERY_DONE] periodType={}, snapshotId={}, contentSize={}, totalElements={}, hasNext={}",
        periodType, publishedSnapshotId.get(), content.size(), totalElements, hasNext);

    return new CursorPageResponsePopularReviewDto(
        content,
        nextCursor,
        nextAfter,
        pageSize,
        totalElements,
        hasNext);
  }

  // periodType과 DomainType=인기 리뷰으로 snapshotid를 뽑아옵니다.
  private Optional<UUID> getPublishedSnapshotId(PeriodType periodType) {
    return aggregateSnapshotRepository
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_REVIEW, periodType, StagingType.PUBLISHED)
        .map(AggregateSnapshot::getSnapshotId);
  }

  private record ParsedCursors(Long cursor, Instant after) {

  }

  private record StringCursors(String cursor, String after) {

  }

  private ParsedCursors parseCursors(String cursor, String after) {
    try {
      if (cursor == null) {
        return new ParsedCursors(null, null);
      }
      return new ParsedCursors(Long.parseLong(cursor), Instant.parse(after));
    } catch (NumberFormatException | DateTimeException e) {
      throw new InvalidCursorValueException();
    }
  }

  private StringCursors getNextCursors(boolean hasNext, List<PopularReviewDto> content) {
    if (hasNext && !content.isEmpty()) {
      PopularReviewDto last = content.get(content.size() - 1);
      // 컨텐츠 내 마지막 요소를 기준으로 next cursor와 보조 커서를 초기화한다.
      return new StringCursors(String.valueOf(last.rank()), String.valueOf(last.createdAt()));
    }
    return new StringCursors(null, null);
  }
}
