package com.codeit.mission.deokhugam.dashboard.popularreviews.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.config.JpaAuditingConfig;
import com.codeit.mission.deokhugam.config.QuerydslConfig;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response.PopularReviewDto;
import com.codeit.mission.deokhugam.dashboard.popularreviews.entity.PopularReview;
import com.codeit.mission.deokhugam.review.entity.Review;
import com.codeit.mission.deokhugam.user.entity.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.Instant;
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
class PopularReviewRepositoryTest {

  private static final UUID SNAPSHOT_ID =
      UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
  private static final UUID OTHER_SNAPSHOT_ID =
      UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");

  @Autowired
  private PopularReviewRepository popularReviewRepository;

  @Autowired
  private EntityManager em;

  @Test
  @DisplayName("해당 스냅샷에 해당하는 PopularReview만 개수 세기 (성공)")
  void countRankingsBySnapshotId_countsTargetSnapshotOnly() {
    // given
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    // 타겟 스냅샷에 해당하는 리뷰 두개
    Review review1 = persistReview("user1@test.com", "user1", "book1", "isbn-1", "review1");
    Review review2 = persistReview("user2@test.com", "user2", "book2", "isbn-2", "review2");
    // 타겟 스냅샷에 해당하지 않는 리뷰 하나
    Review review3 = persistReview("user3@test.com", "user3", "book3", "isbn-3", "review3");

    // 리뷰들을 PopularReview로 가공하고 영속화한다.
    persistPopularReview(review1, 1L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(review2, 2L, 20.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(review3, 1L, 15.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID); // 이건 다른 스냅샷!

    // 영속성 컨텍스트 적용 및 클리어
    em.flush();
    em.clear();

    // 타겟 스냅샷에 존재하는 인기 리뷰 개수를 센다.
    long count = popularReviewRepository.countRankingsBySnapshotId(SNAPSHOT_ID);

    // then
    // 2개의 인기 리뷰가 들어가있는지 확인한다.
    assertEquals(2L, count);
  }

  @Test
  @DisplayName("기간별 인기 리뷰를 점수 기준 내림차순으로 조회 (성공)")
  void findBySnapshotIdDescByScore_returnsRowsOrderedByScore() {
    // given
    // 집계 기간 범위 설정
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    // 세 개의 리뷰 생성
    Review highScoreReview =
        persistReview("high@test.com", "high", "high-book", "isbn-4", "high review");
    Review lowScoreReview =
        persistReview("low@test.com", "low", "low-book", "isbn-5", "low review");
    // 다른 스냅샷
    Review ignoredReview =
        persistReview("ignored@test.com", "ignored", "ignored-book", "isbn-6", "ignored review");
    // 집계 기간에 포함되지 않는 리뷰
    Review outOfPeriod =
        persistReview("out@test.com","outdated", "outdated-book", "isbn-15", "out review");

    // 영속화
    PopularReview highScore =
        persistPopularReview(highScoreReview, 1L, 99.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularReview lowScore =
        persistPopularReview(lowScoreReview, 2L, 45.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(ignoredReview, 1L, 100.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);
    persistPopularReview(
        outOfPeriod,
        1L,
        120.0,
        Instant.parse("2026-04-07T00:00:00Z"),
        Instant.parse("2026-04-13T00:00:00Z"),
        OTHER_SNAPSHOT_ID);

    // 컨텍스트 플러시 & 클리어
    em.flush();
    em.clear();

    // when
    // 타겟 스냅샷과 타겟 기간에 해당하는 인기 리뷰를 점수별로 내림차순 조회함.
    List<PopularReview> result =
        popularReviewRepository.findBySnapshotIdDescByScore(SNAPSHOT_ID);

    // then
    assertEquals(2, result.size()); // 두 개의 인기 리뷰가 산출되었는지?
    assertEquals(highScore.getReviewId(), result.get(0).getReviewId()); // 리스트의 첫 번째 요소가 highScore와 같은지
    assertEquals(lowScore.getReviewId(), result.get(1).getReviewId());
  }

  @Test
  @DisplayName("같은 스냅샷 내에서 동점일 때 rank 오름차순, createdAt 오름차순 조회")
  void findRankingDtosBySnapshotIdAsc_ordersByRankThenCreatedAt() {
    // given
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Review earlyReview =
        persistReview("a@test.com", "a", "book-a", "isbn-7", "early review");
    Review lateReview =
        persistReview("b@test.com", "b", "book-b", "isbn-8", "late review");
    Review ignoredReview =
        persistReview("ignored@test.com", "ignored", "book-c", "isbn-9", "ignored review");

    PopularReview early =
        persistPopularReview(earlyReview, 1L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularReview later =
        persistPopularReview(lateReview, 1L, 29.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(ignoredReview, 1L, 99.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);

    em.flush();
    updateCreatedAt(early.getId(), Instant.parse("2026-04-21T00:00:00Z"));
    updateCreatedAt(later.getId(), Instant.parse("2026-04-21T00:01:00Z"));
    em.clear();

    // when
    List<PopularReviewDto> result =
        popularReviewRepository.findRankingDtosBySnapshotIdAsc(
            SNAPSHOT_ID, null, null, PageRequest.of(0, 10));

    // then
    assertEquals(2, result.size());
    assertEquals("a", result.get(0).userNickname());
    assertEquals("b", result.get(1).userNickname());
    assertEquals("book-a", result.get(0).bookTitle());
    assertEquals("book-b", result.get(1).bookTitle());
    assertTrue((result.get(0).createdAt().isBefore(result.get(1).createdAt())));
  }

  @Test
  @DisplayName("내림차순 조회 시 cursor 와 createdAt 기준 다음 페이지 조회")
  void findRankingDtosBySnapshotIdDesc_appliesCursorAndTieBreak() {
    // given
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Review rankThreeReview =
        persistReview("rank3@test.com", "rank3", "book-rank3", "isbn-10", "rank3 review");
    Review earlyRankTwoReview =
        persistReview("rank2a@test.com", "rank2a", "book-rank2a", "isbn-11", "rank2a review");
    Review lateRankTwoReview =
        persistReview("rank2b@test.com", "rank2b", "book-rank2b", "isbn-12", "rank2b review");
    Review rankOneReview =
        persistReview("rank1@test.com", "rank1", "book-rank1", "isbn-13", "rank1 review");
    Review otherSnapshotReview =
        persistReview("other@test.com", "other", "book-other", "isbn-14", "other review");

    persistPopularReview(rankThreeReview, 3L, 40.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularReview earlyRankTwo =
        persistPopularReview(earlyRankTwoReview, 2L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    PopularReview lateRankTwo =
        persistPopularReview(lateRankTwoReview, 2L, 29.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(rankOneReview, 1L, 20.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(otherSnapshotReview, 1L, 100.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);

    em.flush();
    updateCreatedAt(earlyRankTwo.getId(), Instant.parse("2026-04-21T00:00:00Z"));
    updateCreatedAt(lateRankTwo.getId(), Instant.parse("2026-04-21T00:01:00Z"));
    em.clear();

    // when
    List<PopularReviewDto> result =
        popularReviewRepository.findRankingDtosBySnapshotIdDesc(
            SNAPSHOT_ID,
            2L,
            Instant.parse("2026-04-21T00:01:00Z"),
            PageRequest.of(0, 10));
    // then
    assertEquals(2, result.size());
    assertEquals("rank2a", result.get(0).userNickname());
    assertEquals("rank1", result.get(1).userNickname());
  }

  @Test
  @DisplayName("deleteBySnapshotIdIn deletes only matching popular review rankings")
  void deleteBySnapshotIdIn_deletesTargetSnapshotsOnly() {
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    Instant periodEnd = Instant.parse("2026-04-21T00:00:00Z");

    Review targetReview1 =
        persistReview("delete1@test.com", "delete1", "delete-book-1", "isbn-delete-1",
            "delete review 1");
    Review targetReview2 =
        persistReview("delete2@test.com", "delete2", "delete-book-2", "isbn-delete-2",
            "delete review 2");
    Review remainingReview =
        persistReview("remain@test.com", "remain", "remain-book", "isbn-delete-3",
            "remaining review");

    persistPopularReview(targetReview1, 1L, 30.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(targetReview2, 2L, 20.0, periodStart, periodEnd, SNAPSHOT_ID);
    persistPopularReview(remainingReview, 1L, 10.0, periodStart, periodEnd, OTHER_SNAPSHOT_ID);
    em.flush();

    popularReviewRepository.deleteBySnapshotIdIn(List.of(SNAPSHOT_ID));
    em.flush();
    em.clear();

    assertEquals(0L, popularReviewRepository.countRankingsBySnapshotId(SNAPSHOT_ID));
    assertEquals(1L, popularReviewRepository.countRankingsBySnapshotId(OTHER_SNAPSHOT_ID));
  }

  // 리뷰 객체를 생성하고 영속화하는 메서드
  private Review persistReview(
      String email,
      String nickname,
      String bookTitle,
      String isbn,
      String content) {
    User user = persistUser(email, nickname);
    Book book = persistBook(bookTitle, isbn);
    Review review = Review.builder()
        .book(book)
        .user(user)
        .content(content)
        .rating(5)
        .build();
    em.persist(review);
    return review;
  }

  // 사용자 정보를 받아서 User 객체로 가공하고 영속화하는 메서드
  private User persistUser(String email, String nickname) {
    User user = User.builder()
        .email(email)
        .nickname(nickname)
        .password("password")
        .build();
    em.persist(user);
    return user;
  }

  // 도서 정보를 받아서 Book 객체로 가공하고 영속화하는 메서드
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

  // 리뷰와 점수, 기간, 스냅샷을 받아 인기 리뷰로 변환하고 영속화하는 메서드
  private PopularReview persistPopularReview(
      Review review,
      long rank,
      double score,
      Instant periodStart,
      Instant periodEnd,
      UUID snapshotId) {
    PopularReview popularReview = PopularReview.builder()
        .reviewId(review.getId())
        .periodType(PeriodType.WEEKLY)
        .periodStart(periodStart)
        .periodEnd(periodEnd)
        .rank(rank)
        .score(score)
        .likeCount(3L)
        .commentCount(2L)
        .aggregatedAt(periodEnd)
        .snapshotId(snapshotId)
        .build();
    em.persist(popularReview);
    return popularReview;
  }

  // 인기 리뷰의 생성 날짜를 변경하는 메서드
  private void updateCreatedAt(UUID id, Instant createdAt) {
    int updatedRows =
        em.createQuery("update PopularReview pr set pr.createdAt = :createdAt where pr.id = :id")
            .setParameter("createdAt", createdAt)
            .setParameter("id", id)
            .executeUpdate();

    assertEquals(1, updatedRows);
  }
}
