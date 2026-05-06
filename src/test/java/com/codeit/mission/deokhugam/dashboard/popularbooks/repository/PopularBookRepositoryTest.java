package com.codeit.mission.deokhugam.dashboard.popularbooks.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.config.JpaAuditingConfig;
import com.codeit.mission.deokhugam.config.QuerydslConfig;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.entity.PopularBook;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@Import({JpaAuditingConfig.class, QuerydslConfig.class})
class PopularBookRepositoryTest {

  private static final UUID SNAPSHOT_ID =
      UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
  private static final UUID OTHER_SNAPSHOT_ID =
      UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

  @Autowired
  private PopularBookRepository popularBookRepository;

  @Autowired
  private EntityManager em;

  @Test
  @DisplayName("해당 스냅샷에 해당하는 PopularBook만 개수 세기 (성공)")
  void countRankingsBySnapshotId_countsTargetSnapshotOnly() {
    // given
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Book book1 = persistBook("book1", "isbn-1");
    Book book2 = persistBook("book2", "isbn-2");
    Book book3 = persistBook("book3", "isbn-3");

    persistPopularBook(book1, 1L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(book2, 2L, 20.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(book3, 1L, 15.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);

    em.flush();
    em.clear();

    // when
    long count = popularBookRepository.countRankingsBySnapshotId(SNAPSHOT_ID);

    // then
    assertEquals(2L, count);
  }

  @Test
  @DisplayName("기간별 인기 도서를 점수 기준 내림차순으로 조회 (성공)")
  void findBySnapshotIdDescByScore_returnsRowsOrderedByScore() {
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Book highScoreBook = persistBook("high-book", "isbn-4");
    Book lowScoreBook = persistBook("low-book", "isbn-5");
    Book ignoredBook = persistBook("ignored-book", "isbn-6");
    Book outOfPeriodBook = persistBook("outdated-book", "isbn-7");

    PopularBook highScore =
        persistPopularBook(highScoreBook, 1L, 99.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularBook lowScore =
        persistPopularBook(lowScoreBook, 2L, 45.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(ignoredBook, 1L, 100.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);
    persistPopularBook(
        outOfPeriodBook,
        1L,
        120.0,
        Instant.parse("2026-04-07T00:00:00Z"),
        Instant.parse("2026-04-13T00:00:00Z"),
        OTHER_SNAPSHOT_ID);

    em.flush();
    em.clear();

    List<PopularBook> result =
        popularBookRepository.findBySnapshotIdDescByScore(SNAPSHOT_ID);

    assertEquals(2, result.size());
    assertEquals(highScore.getBookId(), result.get(0).getBookId());
    assertEquals(lowScore.getBookId(), result.get(1).getBookId());
  }

  @Test
  @DisplayName("같은 스냅샷 내에서 동점일 때 rank 오름차순, createdAt 오름차순 조회")
  void findRankingDtosBySnapshotIdAsc_ordersByRankThenCreatedAt() {
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Book earlyBook = persistBook("book-a", "isbn-8");
    Book lateBook = persistBook("book-b", "isbn-9");
    Book ignoredBook = persistBook("book-c", "isbn-10");

    PopularBook early =
        persistPopularBook(earlyBook, 1L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularBook later =
        persistPopularBook(lateBook, 1L, 29.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(ignoredBook, 1L, 99.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);

    em.flush();
    updateCreatedAt(early.getId(), Instant.parse("2026-04-21T00:00:00Z"));
    updateCreatedAt(later.getId(), Instant.parse("2026-04-21T00:01:00Z"));
    em.clear();

    List<PopularBookDto> result =
        popularBookRepository.findRankingDtosBySnapshotIdAsc(
            SNAPSHOT_ID, null, null, PageRequest.of(0, 10));

    assertEquals(2, result.size());
    assertEquals("book-a", result.get(0).title());
    assertEquals("book-b", result.get(1).title());
    assertTrue(result.get(0).createdAt().isBefore(result.get(1).createdAt()));
  }

  @Test
  @DisplayName("내림차순 조회 시 cursor 와 createdAt 기준 다음 페이지 조회")
  void findRankingDtosBySnapshotIdDesc_appliesCursorAndTieBreak() {
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Book rankThreeBook = persistBook("book-rank3", "isbn-11");
    Book earlyRankTwoBook = persistBook("book-rank2a", "isbn-12");
    Book lateRankTwoBook = persistBook("book-rank2b", "isbn-13");
    Book rankOneBook = persistBook("book-rank1", "isbn-14");
    Book otherSnapshotBook = persistBook("book-other", "isbn-15");

    persistPopularBook(rankThreeBook, 3L, 40.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularBook earlyRankTwo =
        persistPopularBook(earlyRankTwoBook, 2L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularBook lateRankTwo =
        persistPopularBook(lateRankTwoBook, 2L, 29.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(rankOneBook, 1L, 20.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(otherSnapshotBook, 1L, 100.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);

    em.flush();
    updateCreatedAt(earlyRankTwo.getId(), Instant.parse("2026-04-21T00:00:00Z"));
    updateCreatedAt(lateRankTwo.getId(), Instant.parse("2026-04-21T00:01:00Z"));
    em.clear();

    List<PopularBookDto> result =
        popularBookRepository.findRankingDtosBySnapshotIdDesc(
            SNAPSHOT_ID,
            2L,
            Instant.parse("2026-04-21T00:01:00Z"),
            PageRequest.of(0, 10));

    assertEquals(2, result.size());
    assertEquals("book-rank2a", result.get(0).title());
    assertEquals("book-rank1", result.get(1).title());
  }

  @Test
  @DisplayName("deleteBySnapshotIdIn deletes only matching popular book rankings")
  void deleteBySnapshotIdIn_deletesTargetSnapshotsOnly() {
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Book targetBook1 = persistBook("target-book-1", "isbn-delete-1");
    Book targetBook2 = persistBook("target-book-2", "isbn-delete-2");
    Book remainingBook = persistBook("remaining-book", "isbn-delete-3");

    persistPopularBook(targetBook1, 1L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(targetBook2, 2L, 20.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularBook(remainingBook, 1L, 10.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);
    em.flush();

    popularBookRepository.deleteBySnapshotIdIn(List.of(SNAPSHOT_ID));
    em.flush();
    em.clear();

    assertEquals(0L, popularBookRepository.countRankingsBySnapshotId(SNAPSHOT_ID));
    assertEquals(1L, popularBookRepository.countRankingsBySnapshotId(OTHER_SNAPSHOT_ID));
  }

  private Book persistBook(String title, String isbn) {
    Book book = Book.builder()
        .title(title)
        .author("author")
        .description("description")
        .publisher("publisher")
        .publishedDate(LocalDate.of(2026, 4, 1))
        .isbn(isbn)
        .thumbnailUrl("thumbnail")
        .reviewCount(0)
        .rating(0.0)
        .build();
    em.persist(book);
    return book;
  }

  private PopularBook persistPopularBook(
      Book book,
      long rank,
      double score,
      Instant periodStart,
      Instant periodEnd,
      UUID snapshotId) {
    PopularBook popularBook = PopularBook.builder()
        .bookId(book.getId())
        .periodType(PeriodType.WEEKLY)
        .periodStart(periodStart)
        .periodEnd(periodEnd)
        .reviewCount(3L)
        .avgRating(4.5)
        .score(score)
        .rank(rank)
        .snapshotId(snapshotId)
        .build();
    em.persist(popularBook);
    return popularBook;
  }

  private void updateCreatedAt(UUID id, Instant createdAt) {
    int updatedRows =
        em.createQuery("update PopularBook pb set pb.createdAt = :createdAt where pb.id = :id")
            .setParameter("createdAt", createdAt)
            .setParameter("id", id)
            .executeUpdate();

    assertEquals(1, updatedRows);
  }
}
