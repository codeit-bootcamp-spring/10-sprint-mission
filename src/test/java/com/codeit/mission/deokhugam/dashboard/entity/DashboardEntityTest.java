package com.codeit.mission.deokhugam.dashboard.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.popularbooks.entity.PopularBook;
import com.codeit.mission.deokhugam.dashboard.popularreviews.entity.PopularReview;
import com.codeit.mission.deokhugam.dashboard.powerusers.entity.PowerUser;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DashboardEntityTest {

  private static final Instant START = Instant.parse("2026-04-26T00:00:00Z");
  private static final Instant END = Instant.parse("2026-04-27T00:00:00Z");
  private static final UUID SNAPSHOT_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Test
  @DisplayName("popular book builder maps fields and updates rank")
  void popularBook() {
    UUID bookId = UUID.randomUUID();

    PopularBook result = PopularBook.builder()
        .bookId(bookId)
        .periodStart(START)
        .periodEnd(END)
        .reviewCount(12L)
        .avgRating(4.2)
        .score(7.3)
        .rank(0L)
        .periodType(PeriodType.DAILY)
        .snapshotId(SNAPSHOT_ID)
        .build();
    result.updateRank(3L);

    assertThat(result.getBookId()).isEqualTo(bookId);
    assertThat(result.getPeriodStart()).isEqualTo(START);
    assertThat(result.getPeriodEnd()).isEqualTo(END);
    assertThat(result.getReviewCount()).isEqualTo(12L);
    assertThat(result.getAvgRating()).isEqualTo(4.2);
    assertThat(result.getScore()).isEqualTo(7.3);
    assertThat(result.getRank()).isEqualTo(3L);
    assertThat(result.getPeriodType()).isEqualTo(PeriodType.DAILY);
    assertThat(result.getSnapshotId()).isEqualTo(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("popular review builder maps fields and updates rank")
  void popularReview() {
    UUID reviewId = UUID.randomUUID();

    PopularReview result = PopularReview.builder()
        .reviewId(reviewId)
        .periodType(PeriodType.WEEKLY)
        .periodStart(START)
        .periodEnd(END)
        .score(9.1)
        .rank(0L)
        .likeCount(4L)
        .commentCount(5L)
        .aggregatedAt(END)
        .snapshotId(SNAPSHOT_ID)
        .build();
    result.updateRank(2L);

    assertThat(result.getReviewId()).isEqualTo(reviewId);
    assertThat(result.getPeriodType()).isEqualTo(PeriodType.WEEKLY);
    assertThat(result.getPeriodStart()).isEqualTo(START);
    assertThat(result.getPeriodEnd()).isEqualTo(END);
    assertThat(result.getScore()).isEqualTo(9.1);
    assertThat(result.getRank()).isEqualTo(2L);
    assertThat(result.getLikeCount()).isEqualTo(4L);
    assertThat(result.getCommentCount()).isEqualTo(5L);
    assertThat(result.getAggregatedAt()).isEqualTo(END);
    assertThat(result.getSnapshotId()).isEqualTo(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("power user builder maps fields and updates rank")
  void powerUser() {
    UUID userId = UUID.randomUUID();

    PowerUser result = PowerUser.builder()
        .userId(userId)
        .periodType(PeriodType.MONTHLY)
        .periodStart(START)
        .periodEnd(END)
        .rank(0L)
        .score(11.4)
        .reviewScoreSum(8.0)
        .likeCount(6L)
        .commentCount(7L)
        .aggregatedAt(END)
        .snapshotId(SNAPSHOT_ID)
        .build();
    result.updateRank(1L);

    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getPeriodType()).isEqualTo(PeriodType.MONTHLY);
    assertThat(result.getPeriodStart()).isEqualTo(START);
    assertThat(result.getPeriodEnd()).isEqualTo(END);
    assertThat(result.getRank()).isEqualTo(1L);
    assertThat(result.getScore()).isEqualTo(11.4);
    assertThat(result.getReviewScoreSum()).isEqualTo(8.0);
    assertThat(result.getLikeCount()).isEqualTo(6L);
    assertThat(result.getCommentCount()).isEqualTo(7L);
    assertThat(result.getAggregatedAt()).isEqualTo(END);
    assertThat(result.getSnapshotId()).isEqualTo(SNAPSHOT_ID);
  }
}
