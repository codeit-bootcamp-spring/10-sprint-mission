package com.codeit.mission.deokhugam.dashboard.popularbooks.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.repository.PopularBookRepository;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshot;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotRepository;
import com.codeit.mission.deokhugam.error.DeokhugamException;
import com.codeit.mission.deokhugam.error.ErrorCode;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class PopularBookServiceTest {

  private static final UUID WEEKLY_SNAPSHOT_ID =
      UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

  @Mock
  private PopularBookRepository popularBookRepository;

  @Mock
  private AggregateSnapshotRepository aggregateSnapshotRepository;

  @InjectMocks
  private PopularBookService popularBookService;

  @Test
  @DisplayName("인기 도서 첫 페이지를 오름차순으로 조회한다")
  void getBooks_firstPageAsc() {
    Instant createdAt1 = Instant.parse("2026-04-27T14:30:00Z");
    Instant createdAt2 = Instant.parse("2026-04-27T14:31:00Z");
    PopularBookDto book1 = popularBookDto("book-1", "author-1", PeriodType.WEEKLY, createdAt1, 1L,
        33.0);
    PopularBookDto book2 = popularBookDto("book-2", "author-2", PeriodType.WEEKLY, createdAt2, 2L,
        24.0);

    when(
        aggregateSnapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(PeriodType.WEEKLY, WEEKLY_SNAPSHOT_ID)));
    when(popularBookRepository.findRankingDtosBySnapshotIdAsc(
        WEEKLY_SNAPSHOT_ID, null, null, PageRequest.of(0, 51)))
        .thenReturn(List.of(book1, book2));
    when(popularBookRepository.countRankingsBySnapshotId(WEEKLY_SNAPSHOT_ID)).thenReturn(2L);

    CursorPageResponsePopularBookDto result =
        popularBookService.get(PeriodType.WEEKLY, DirectionEnum.ASC, null, null, 50);

    assertEquals(List.of(book1, book2), result.content());
    assertEquals(50, result.size());
    assertEquals(2L, result.totalElements());
    assertFalse(result.hasNext());
    assertNull(result.nextCursor());
    assertNull(result.nextAfter());
    verify(aggregateSnapshotRepository)
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED);
    verify(popularBookRepository)
        .findRankingDtosBySnapshotIdAsc(WEEKLY_SNAPSHOT_ID, null, null, PageRequest.of(0, 51));
    verify(popularBookRepository).countRankingsBySnapshotId(WEEKLY_SNAPSHOT_ID);
  }

  @Test
  @DisplayName("인기 도서 첫 페이지를 내림차순으로 조회한다")
  void getBooks_firstPageDesc() {
    Instant createdAt1 = Instant.parse("2026-04-27T14:30:00Z");
    Instant createdAt2 = Instant.parse("2026-04-27T14:31:00Z");
    PopularBookDto book1 = popularBookDto("book-1", "author-1", PeriodType.WEEKLY, createdAt1, 1L,
        33.0);
    PopularBookDto book2 = popularBookDto("book-2", "author-2", PeriodType.WEEKLY, createdAt2, 2L,
        24.0);

    when(
        aggregateSnapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(PeriodType.WEEKLY, WEEKLY_SNAPSHOT_ID)));
    when(popularBookRepository.findRankingDtosBySnapshotIdDesc(
        WEEKLY_SNAPSHOT_ID, null, null, PageRequest.of(0, 51)))
        .thenReturn(List.of(book2, book1));
    when(popularBookRepository.countRankingsBySnapshotId(WEEKLY_SNAPSHOT_ID)).thenReturn(2L);

    CursorPageResponsePopularBookDto result =
        popularBookService.get(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 50);

    assertEquals(List.of(book2, book1), result.content());
    assertEquals(50, result.size());
    assertEquals(2L, result.totalElements());
    assertFalse(result.hasNext());
    assertNull(result.nextCursor());
    assertNull(result.nextAfter());
    verify(aggregateSnapshotRepository)
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED);
    verify(popularBookRepository)
        .findRankingDtosBySnapshotIdDesc(WEEKLY_SNAPSHOT_ID, null, null, PageRequest.of(0, 51));
    verify(popularBookRepository).countRankingsBySnapshotId(WEEKLY_SNAPSHOT_ID);
  }

  @Test
  @DisplayName("다음 페이지가 있으면 next cursor를 반환한다")
  void getBooks_returnsNextCursor() {
    Instant createdAt1 = Instant.parse("2026-04-27T14:30:00Z");
    Instant createdAt2 = Instant.parse("2026-04-27T14:31:00Z");
    Instant createdAt3 = Instant.parse("2026-04-27T14:32:00Z");
    PopularBookDto book1 = popularBookDto("book-1", "author-1", PeriodType.WEEKLY, createdAt1, 1L,
        10.0);
    PopularBookDto book2 = popularBookDto("book-2", "author-2", PeriodType.WEEKLY, createdAt2, 2L,
        9.0);
    PopularBookDto book3 = popularBookDto("book-3", "author-3", PeriodType.WEEKLY, createdAt3, 3L,
        8.0);

    when(
        aggregateSnapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(PeriodType.WEEKLY, WEEKLY_SNAPSHOT_ID)));
    when(popularBookRepository.findRankingDtosBySnapshotIdAsc(
        WEEKLY_SNAPSHOT_ID, null, null, PageRequest.of(0, 3)))
        .thenReturn(List.of(book1, book2, book3));
    when(popularBookRepository.countRankingsBySnapshotId(WEEKLY_SNAPSHOT_ID)).thenReturn(3L);

    CursorPageResponsePopularBookDto result =
        popularBookService.get(PeriodType.WEEKLY, DirectionEnum.ASC, null, null, 2);

    assertTrue(result.hasNext());
    assertEquals(List.of(book1, book2), result.content());
    assertEquals("2", result.nextCursor());
    assertEquals(createdAt2.toString(), result.nextAfter());
    assertEquals(2, result.size());
    assertEquals(3L, result.totalElements());
  }

  @Test
  @DisplayName("cursor 형식이 올바르지 않으면 예외가 발생한다")
  void getBooks_invalidCursor() {
    DeokhugamException exception =
        assertThrows(
            DeokhugamException.class,
            () ->
                popularBookService.get(
                    PeriodType.WEEKLY,
                    DirectionEnum.DESC,
                    "invalid-cursor",
                    "2026-04-27T14:30:00Z",
                    50));

    assertEquals(ErrorCode.CURSOR_OR_AFTER_FORMAT_NOT_VALID, exception.getErrorCode());
    verifyNoInteractions(popularBookRepository, aggregateSnapshotRepository);
  }

  @Test
  @DisplayName("after 형식이 올바르지 않으면 예외가 발생한다")
  void getBooks_invalidAfter() {
    DeokhugamException exception =
        assertThrows(
            DeokhugamException.class,
            () ->
                popularBookService.get(
                    PeriodType.WEEKLY, DirectionEnum.DESC, "3", "not-a-date", 50));

    assertEquals(ErrorCode.CURSOR_OR_AFTER_FORMAT_NOT_VALID, exception.getErrorCode());
    verifyNoInteractions(popularBookRepository, aggregateSnapshotRepository);
  }

  @Test
  @DisplayName("cursor와 after는 함께 제공되어야 한다")
  void getBooks_cursorAfterMismatch() {
    DeokhugamException exception =
        assertThrows(
            DeokhugamException.class,
            () ->
                popularBookService.get(
                    PeriodType.WEEKLY, DirectionEnum.DESC, "3", null, 50));

    assertEquals(ErrorCode.CURSOR_AFTER_NOT_PROVIDED_TOGETHER, exception.getErrorCode());
    verifyNoInteractions(popularBookRepository, aggregateSnapshotRepository);
  }

  @Test
  @DisplayName("발행된 스냅샷이 없으면 빈 content를 반환한다")
  void getBooks_noSnapshotReturnsEmptyContent() {
    when(
        aggregateSnapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.MONTHLY, StagingType.PUBLISHED))
        .thenReturn(Optional.empty());

    CursorPageResponsePopularBookDto result =
        popularBookService.get(PeriodType.MONTHLY, DirectionEnum.DESC, null, null, 1);

    assertTrue(result.content().isEmpty());
    assertEquals(1, result.size());
    assertEquals(0L, result.totalElements());
    assertFalse(result.hasNext());
    assertNull(result.nextCursor());
    assertNull(result.nextAfter());
    verify(aggregateSnapshotRepository)
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            DomainType.POPULAR_BOOK, PeriodType.MONTHLY, StagingType.PUBLISHED);
    verifyNoInteractions(popularBookRepository);
  }

  @Test
  @DisplayName("limit가 허용 범위를 벗어나면 예외가 발생한다")
  void getBooks_invalidLimit() {
    DeokhugamException exception =
        assertThrows(
            DeokhugamException.class,
            () -> popularBookService.get(PeriodType.WEEKLY, DirectionEnum.ASC, null, null, 101));

    assertEquals(ErrorCode.ILLEGAL_LIMIT_VALUE, exception.getErrorCode());
    verifyNoInteractions(popularBookRepository, aggregateSnapshotRepository);
  }

  private AggregateSnapshot publishedSnapshot(PeriodType periodType, UUID snapshotId) {
    return AggregateSnapshot.builder()
        .domainType(DomainType.POPULAR_BOOK)
        .snapshotId(snapshotId)
        .periodType(periodType)
        .aggregatedAt(Instant.parse("2026-04-15T00:00:00Z"))
        .stagingType(StagingType.PUBLISHED)
        .build();
  }

  private PopularBookDto popularBookDto(
      String title,
      String author,
      PeriodType periodType,
      Instant createdAt,
      long rank,
      double score) {
    return new PopularBookDto(
        UUID.randomUUID(),
        UUID.randomUUID(),
        title,
        author,
        "thumbnail",
        periodType,
        rank,
        score,
        3L,
        4.5,
        createdAt);
  }
}
