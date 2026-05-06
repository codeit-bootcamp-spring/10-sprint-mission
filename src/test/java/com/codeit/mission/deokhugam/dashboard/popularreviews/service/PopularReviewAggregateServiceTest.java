package com.codeit.mission.deokhugam.dashboard.popularreviews.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.comment.repository.CommentRepository;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.request.ReviewCommentCount;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.request.ReviewLikeCount;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.request.ReviewStat;
import com.codeit.mission.deokhugam.dashboard.popularreviews.entity.PopularReview;
import com.codeit.mission.deokhugam.dashboard.popularreviews.repository.PopularReviewRepository;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.ApplicationEventPublisher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PopularReviewAggregateServiceTest {

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private PopularReviewRepository popularReviewRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @InjectMocks
  private PopularReviewAggregateService popularReviewAggregateService;

  @Test
  @DisplayName("인기 리뷰 집계에 필요한 지수들을 일괄 로딩하는 테스트 (성공)")
  public void loadReviewStat_success() {
    // given
    Instant aggregatedAt = Instant.parse("2026-04-23T15:30:00Z");
    Instant periodStart = Instant.parse("2026-04-16T15:30:00Z");
    Instant periodEnd = Instant.parse("2026-04-23T15:30:00Z");

    // 두 명의 사용자 ID 생성
    UUID higherReviewId = UUID.randomUUID();
    UUID lowerReviewId = UUID.randomUUID();

    // 리뷰 별 댓글 수 설정
    when(commentRepository.findReviewCommentCounts(periodStart, periodEnd))
        .thenReturn(List.of(
                new ReviewCommentCount(higherReviewId, 5L),
                new ReviewCommentCount(lowerReviewId, 1L)
            )
        );

    // 리뷰 별 좋아요 수 설정
    when(reviewRepository.countReviewLikes(periodStart, periodEnd, ReviewStatus.ACTIVE))
        .thenReturn(List.of(
                new ReviewLikeCount(higherReviewId, 3L),
                new ReviewLikeCount(lowerReviewId, 0L)
            )
        );

    // when
    // 리뷰 별 스탯을 구한다.
    Map<UUID, ReviewStat> statsPerReview = popularReviewAggregateService.loadReviewStat(
        PeriodType.WEEKLY, periodEnd);

    // then
    assertEquals(2, statsPerReview.size()); // 요소 개수가 2인지?
    assertEquals(higherReviewId, statsPerReview.get(higherReviewId).reviewId()); // ID 가 일치하는지?
    assertEquals(lowerReviewId, statsPerReview.get(lowerReviewId).reviewId());
    // 각종 스탯들 검증
    assertEquals(3L, statsPerReview.get(higherReviewId).likeCount());
    assertEquals(5L, statsPerReview.get(higherReviewId).commentCount());
    assertEquals(0L, statsPerReview.get(lowerReviewId).likeCount());
    assertEquals(1L, statsPerReview.get(lowerReviewId).commentCount());
    verify(commentRepository).findReviewCommentCounts(periodStart, periodEnd);
    verify(reviewRepository).countReviewLikes(periodStart, periodEnd, ReviewStatus.ACTIVE);
  }

  @Test
  @DisplayName("주중 시각을 기준으로 ")
  void rankPopularReviews_findsRowsBySnapshotId() {
    // given
    // 주중 집계 시간
    Instant aggregatedAt = Instant.parse("2026-04-23T15:30:00Z");
    // 주간이므로 집계시간으로 부터 7일 이전이 periodStart가 된다.
    // 스냅샷 ID
    UUID snapshotId = UUID.randomUUID();

    when(popularReviewRepository.findBySnapshotIdDescByScore(snapshotId))
        .thenReturn(List.of());

    // when
    popularReviewAggregateService.rankPopularReviews(PeriodType.WEEKLY, aggregatedAt, snapshotId);

    verify(popularReviewRepository).findBySnapshotIdDescByScore(snapshotId);
  }

  @Test
  @DisplayName("loadReviewStat merges review ids from comments and likes")
  void loadReviewStat_mergesReviewIdsFromBothSources() {
    // given
    Instant aggregatedAt = Instant.parse("2026-04-21T00:00:00Z");
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    UUID commentOnlyReviewId = UUID.randomUUID(); // 댓글만 존재하는 리뷰의 ID
    UUID likeOnlyReviewId = UUID.randomUUID(); // 좋아요만 존재하는 리뷰의 ID

    // 리뷰의 댓글 개수를 구하는 레포지토리 메서드 호출 시 해당 값 리턴
    when(commentRepository.findReviewCommentCounts(periodStart, aggregatedAt))
        .thenReturn(List.of(new ReviewCommentCount(commentOnlyReviewId, 2L))); // 댓글 2개
    // 리뷰의 좋아요 개수를 구하는 레포지토리 메서드 호출 시 해당 값 리턴
    when(reviewRepository.countReviewLikes(periodStart, aggregatedAt, ReviewStatus.ACTIVE))
        .thenReturn(List.of(new ReviewLikeCount(likeOnlyReviewId, 4L))); // 좋아요 4개

    // when
    // 리뷰 별 스탯 구하기
    Map<UUID, ReviewStat> statsPerReview =
        popularReviewAggregateService.loadReviewStat(PeriodType.WEEKLY, aggregatedAt);

    // then
    assertEquals(2, statsPerReview.size()); // 요소의 개수는 두개
    // 댓글만 달린 리뷰의 댓글 좋아요 개수와 댓글 개수 검증 (댓글 2개)
    assertEquals(0L, statsPerReview.get(commentOnlyReviewId).likeCount());
    assertEquals(2L, statsPerReview.get(commentOnlyReviewId).commentCount());
    // 좋아요만 달린 리뷰의 좋아요 개수와 댓글 개수 검증 (좋아요 4개)
    assertEquals(4L, statsPerReview.get(likeOnlyReviewId).likeCount());
    assertEquals(0L, statsPerReview.get(likeOnlyReviewId).commentCount());
  }

  @Test
  @DisplayName("같은 랭크를 가진 리뷰를 조회할 때 타이브레이킹 (성공)")
  void rankPopularReviews_assignsSequentialRankForSameScore() {
    // given
    Instant aggregatedAt = Instant.parse("2026-04-21T00:00:00Z");
    Instant periodStart = Instant.parse("2026-04-14T00:00:00Z");
    UUID snapshotId = UUID.randomUUID(); // 스냅샷 ID 생성

    // 해당 스냅샷을 참조하는 세 개의 리뷰(first와 second는 동점)
    PopularReview first = createPopularReview(UUID.randomUUID(), 9.0, snapshotId, aggregatedAt);
    PopularReview second = createPopularReview(UUID.randomUUID(), 9.0, snapshotId, aggregatedAt);
    PopularReview third = createPopularReview(UUID.randomUUID(), 7.0, snapshotId, aggregatedAt);

    // 점수 별 내림차순으로 인기 리뷰를 가져올 때 1, 2, 3 순으로 가져옴
    // first와 second는 동점이지만 first의 createdAt이 더 빠르므로 first가 우선이 된다.
    when(popularReviewRepository.findBySnapshotIdDescByScore(snapshotId))
        .thenReturn(List.of(first, second, third));

    // when
    // 주간 기간 내에 해당 스냅샷을 참조하는 리뷰들에게 랭크를 부여한다
    popularReviewAggregateService.rankPopularReviews(PeriodType.WEEKLY, aggregatedAt, snapshotId);

    assertEquals(1L, first.getRank());
    assertEquals(2L, second.getRank());
    assertEquals(3L, third.getRank());
    // 해당 레포지토리 메서드가 한번 수행 되었는지 검증
    verify(popularReviewRepository).findBySnapshotIdDescByScore(snapshotId);
  }

  @Test
  @DisplayName("리뷰 정보를 바탕으로 인기 리뷰 객체 생성 테스트 (성공)")
  void toPopularReview_buildsPopularReviewFromStat() {
    // given
    // 리뷰의 정보와 해당 리뷰의 stat을 생성한다
    UUID reviewId = UUID.randomUUID();
    UUID snapshotId = UUID.randomUUID();
    Instant aggregatedAt = Instant.parse("2026-04-21T00:00:00Z");
    ReviewStat stat = new ReviewStat(reviewId, 4L, 2L);

    // when
    // 만든 리뷰를 바탕으로 PopularReview로 가공한다.
    PopularReview popularReview = popularReviewAggregateService.toPopularReview(
        reviewId,
        stat,
        PeriodType.WEEKLY,
        aggregatedAt,
        snapshotId);

    // then
    assertEquals(reviewId, popularReview.getReviewId());
    assertEquals(PeriodType.WEEKLY, popularReview.getPeriodType());
    assertEquals(Instant.parse("2026-04-14T00:00:00Z"), popularReview.getPeriodStart());
    assertEquals(aggregatedAt, popularReview.getPeriodEnd());
    assertEquals(0L, popularReview.getRank());
    assertEquals(2.6d, popularReview.getScore(), 1e-9);
    assertEquals(4L, popularReview.getLikeCount());
    assertEquals(2L, popularReview.getCommentCount());
    assertEquals(aggregatedAt, popularReview.getAggregatedAt());
    assertEquals(snapshotId, popularReview.getSnapshotId());
  }

  @Test
  @DisplayName("텅 빈 리뷰 스탯은 0을 반환 (성공)")
  void emptyStat_returnsZeroCounts() {
    // given
    UUID reviewId = UUID.randomUUID();

    // when
    ReviewStat stat = popularReviewAggregateService.emptyStat(reviewId);

    // then
    assertEquals(reviewId, stat.reviewId());
    assertEquals(0L, stat.likeCount());
    assertEquals(0L, stat.commentCount());
  }

  // 리뷰 정보로부터 인기 리뷰 객체를 생성하고 반환하는 메서드
  private PopularReview createPopularReview(
      UUID reviewId,
      double score,
      UUID snapshotId,
      Instant aggregatedAt) {
    return PopularReview.builder()
        .reviewId(reviewId)
        .periodType(PeriodType.WEEKLY)
        .periodStart(Instant.parse("2026-04-14T00:00:00Z"))
        .periodEnd(aggregatedAt)
        .rank(0L)
        .score(score)
        .likeCount(0L)
        .commentCount(0L)
        .aggregatedAt(aggregatedAt)
        .snapshotId(snapshotId)
        .build();
  }

}
