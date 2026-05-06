package com.codeit.mission.deokhugam.dashboard.powerusers.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.comment.repository.CommentRepository;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.request.PowerUserLikeCount;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.request.UserCommentCount;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.request.UserReviewAggregate;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.request.UserStat;
import com.codeit.mission.deokhugam.dashboard.powerusers.entity.PowerUser;
import com.codeit.mission.deokhugam.dashboard.powerusers.repository.PowerUserRepository;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.repository.ReviewLikeRepository;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import com.codeit.mission.deokhugam.user.entity.User;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PowerUserAggregateServiceTest {

  private static final Instant AGGREGATED_AT = Instant.parse("2026-04-27T00:00:00Z");
  private static final UUID SNAPSHOT_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private ReviewRepository reviewRepository;

  @Mock
  private ReviewLikeRepository reviewLikeRepository;

  @Mock
  private PowerUserRepository powerUserRepository;

  @InjectMocks
  private PowerUserAggregateService powerUserAggregateService;

  @Test
  @DisplayName("loadUserStats merges comment review and like aggregates by user")
  void loadUserStats_mergesAggregateRows() {
    UUID commentOnlyUserId = UUID.randomUUID();
    UUID reviewAndLikeUserId = UUID.randomUUID();
    Instant periodStart = PeriodType.WEEKLY.calculateStart(AGGREGATED_AT);

    when(commentRepository.findUserCommentCounts(periodStart, AGGREGATED_AT))
        .thenReturn(List.of(new UserCommentCount(commentOnlyUserId, 3L)));
    when(reviewRepository.findUserReviewAggregates(
        periodStart, AGGREGATED_AT, ReviewStatus.ACTIVE))
        .thenReturn(List.of(new UserReviewAggregate(reviewAndLikeUserId, 10.0)));
    when(reviewLikeRepository.findUserLikeCounts(periodStart, AGGREGATED_AT))
        .thenReturn(List.of(new PowerUserLikeCount(reviewAndLikeUserId, 4L)));

    Map<UUID, UserStat> result =
        powerUserAggregateService.loadUserStats(PeriodType.WEEKLY, AGGREGATED_AT);

    assertThat(result).hasSize(2);
    assertThat(result.get(commentOnlyUserId).commentCount()).isEqualTo(3L);
    assertThat(result.get(commentOnlyUserId).score()).isCloseTo(0.9, within(0.0001));
    assertThat(result.get(reviewAndLikeUserId).reviewScoreSum()).isEqualTo(10.0);
    assertThat(result.get(reviewAndLikeUserId).likeCount()).isEqualTo(4L);
    assertThat(result.get(reviewAndLikeUserId).score()).isCloseTo(5.8, within(0.0001));
  }

  @Test
  @DisplayName("rankPowerUsers updates ranks in score order")
  void rankPowerUsers_updatesRank() {
    PowerUser first = powerUser(UUID.randomUUID(), 0L);
    PowerUser second = powerUser(UUID.randomUUID(), 0L);
    when(powerUserRepository.findBySnapshotIdDescByScore(SNAPSHOT_ID))
        .thenReturn(List.of(first, second));

    powerUserAggregateService.rankPowerUsers(PeriodType.DAILY, AGGREGATED_AT, SNAPSHOT_ID);

    assertThat(first.getRank()).isEqualTo(1L);
    assertThat(second.getRank()).isEqualTo(2L);
    verify(powerUserRepository).findBySnapshotIdDescByScore(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("toPowerUser maps user stat and period window")
  void toPowerUser_mapsFields() {
    UUID userId = UUID.randomUUID();
    User user = User.builder().nickname("power").build();
    ReflectionTestUtils.setField(user, "id", userId);
    UserStat stat = new UserStat(userId, 8.0, 5L, 2L, 5.6);

    PowerUser result = powerUserAggregateService.toPowerUser(
        user, stat, PeriodType.MONTHLY, AGGREGATED_AT, SNAPSHOT_ID);

    assertThat(result.getUserId()).isEqualTo(userId);
    assertThat(result.getPeriodType()).isEqualTo(PeriodType.MONTHLY);
    assertThat(result.getPeriodStart()).isEqualTo(PeriodType.MONTHLY.calculateStart(AGGREGATED_AT));
    assertThat(result.getPeriodEnd()).isEqualTo(AGGREGATED_AT);
    assertThat(result.getRank()).isZero();
    assertThat(result.getScore()).isEqualTo(5.6);
    assertThat(result.getReviewScoreSum()).isEqualTo(8.0);
    assertThat(result.getLikeCount()).isEqualTo(5L);
    assertThat(result.getCommentCount()).isEqualTo(2L);
    assertThat(result.getAggregatedAt()).isEqualTo(AGGREGATED_AT);
    assertThat(result.getSnapshotId()).isEqualTo(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("emptyStat returns zero stat for user")
  void emptyStat() {
    UUID userId = UUID.randomUUID();

    UserStat result = powerUserAggregateService.emptyStat(userId);

    assertThat(result.userId()).isEqualTo(userId);
    assertThat(result.reviewScoreSum()).isZero();
    assertThat(result.likeCount()).isZero();
    assertThat(result.commentCount()).isZero();
    assertThat(result.score()).isZero();
  }

  private PowerUser powerUser(UUID userId, long rank) {
    return PowerUser.builder()
        .userId(userId)
        .periodType(PeriodType.DAILY)
        .periodStart(PeriodType.DAILY.calculateStart(AGGREGATED_AT))
        .periodEnd(AGGREGATED_AT)
        .rank(rank)
        .score(10.0)
        .reviewScoreSum(8.0)
        .likeCount(2L)
        .commentCount(3L)
        .aggregatedAt(AGGREGATED_AT)
        .snapshotId(SNAPSHOT_ID)
        .build();
  }
}
