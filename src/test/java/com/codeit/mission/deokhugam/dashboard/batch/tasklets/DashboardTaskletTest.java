package com.codeit.mission.deokhugam.dashboard.batch.tasklets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.popularbooks.batch.tasklet.RankPopularBookTasklet;
import com.codeit.mission.deokhugam.dashboard.popularbooks.service.PopularBookAggregationService;
import com.codeit.mission.deokhugam.dashboard.popularreviews.batch.tasklet.RankPopularReviewTasklet;
import com.codeit.mission.deokhugam.dashboard.popularreviews.service.PopularReviewAggregateService;
import com.codeit.mission.deokhugam.dashboard.powerusers.batch.tasklet.RankPowerUsersTasklet;
import com.codeit.mission.deokhugam.dashboard.powerusers.service.PowerUserAggregateService;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshot;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotService;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.test.util.ReflectionTestUtils;

class DashboardTaskletTest {

  private static final Instant AGGREGATED_AT = Instant.parse("2026-04-27T00:00:00Z");
  private static final UUID SNAPSHOT_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");

  @Test
  @DisplayName("create snapshot tasklet creates staging snapshot and stores context values")
  void createNewSnapshotTasklet() throws Exception {
    AggregateSnapshotService service = mock(AggregateSnapshotService.class);
    AggregateSnapshot snapshot = AggregateSnapshot.builder()
        .snapshotId(SNAPSHOT_ID)
        .domainType(DomainType.POPULAR_BOOK)
        .periodType(PeriodType.WEEKLY)
        .aggregatedAt(AGGREGATED_AT)
        .stagingType(StagingType.STAGING)
        .build();
    when(service.createStagingSnapshot(DomainType.POPULAR_BOOK, PeriodType.WEEKLY, AGGREGATED_AT))
        .thenReturn(snapshot);
    CreateNewSnapshotTasklet tasklet = new CreateNewSnapshotTasklet(service);
    setField(tasklet, "periodTypeValue", "WEEKLY");
    setField(tasklet, "aggregatedAtValue", AGGREGATED_AT.toString());
    setField(tasklet, "domainTypeValue", "POPULAR_BOOK");
    ChunkContext chunkContext = chunkContext();

    RepeatStatus result = tasklet.execute(null, chunkContext);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(service).createStagingSnapshot(DomainType.POPULAR_BOOK, PeriodType.WEEKLY,
        AGGREGATED_AT);
    assertThat(chunkContext.getStepContext().getStepExecution().getJobExecution()
        .getExecutionContext().getString("snapshotId")).isEqualTo(SNAPSHOT_ID.toString());
    assertThat(chunkContext.getStepContext().getStepExecution().getJobExecution()
        .getExecutionContext().getString("domainType")).isEqualTo("POPULAR_BOOK");
  }

  @Test
  @DisplayName("publish snapshot tasklet publishes snapshot")
  void publishSnapshotTasklet() throws Exception {
    AggregateSnapshotService service = mock(AggregateSnapshotService.class);
    PublishSnapshotTasklet tasklet = new PublishSnapshotTasklet(service);
    setField(tasklet, "snapshotIdValue", SNAPSHOT_ID.toString());
    setField(tasklet, "domainTypeValue", "POWER_USER");

    RepeatStatus result = tasklet.execute(null, null);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(service).publishSnapshot(DomainType.POWER_USER, SNAPSHOT_ID);
  }

  @Test
  @DisplayName("cleanup old snapshots tasklet delegates parsed parameters")
  void cleanupOldSnapshotsTasklet() throws Exception {
    AggregateSnapshotService service = mock(AggregateSnapshotService.class);
    CleanupOldSnapshotsTasklet tasklet = new CleanupOldSnapshotsTasklet(service);
    setField(tasklet, "periodTypeValue", "MONTHLY");
    setField(tasklet, "domainTypeValue", "POPULAR_REVIEW");
    setField(tasklet, "keepCount", 3);

    RepeatStatus result = tasklet.execute(null, null);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(service).cleanupOldSnapshots(DomainType.POPULAR_REVIEW, PeriodType.MONTHLY, 3);
  }

  @Test
  @DisplayName("rank popular book tasklet delegates parsed parameters")
  void rankPopularBookTasklet() throws Exception {
    PopularBookAggregationService service = mock(PopularBookAggregationService.class);
    RankPopularBookTasklet tasklet = new RankPopularBookTasklet(service);
    setRankingFields(tasklet, "DAILY");

    RepeatStatus result = tasklet.execute(null, null);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(service).rankPopularBooks(PeriodType.DAILY, AGGREGATED_AT, SNAPSHOT_ID);
  }

  @Test
  @DisplayName("rank popular review tasklet delegates parsed parameters")
  void rankPopularReviewTasklet() {
    PopularReviewAggregateService service = mock(PopularReviewAggregateService.class);
    RankPopularReviewTasklet tasklet = new RankPopularReviewTasklet(service);
    setRankingFields(tasklet, "WEEKLY");

    RepeatStatus result = tasklet.execute(null, null);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(service).rankPopularReviews(PeriodType.WEEKLY, AGGREGATED_AT, SNAPSHOT_ID);
  }

  @Test
  @DisplayName("rank power users tasklet delegates parsed parameters")
  void rankPowerUsersTasklet() throws Exception {
    PowerUserAggregateService service = mock(PowerUserAggregateService.class);
    RankPowerUsersTasklet tasklet = new RankPowerUsersTasklet(service);
    setRankingFields(tasklet, "ALL_TIME");

    RepeatStatus result = tasklet.execute(null, null);

    assertThat(result).isEqualTo(RepeatStatus.FINISHED);
    verify(service).rankPowerUsers(PeriodType.ALL_TIME, AGGREGATED_AT, SNAPSHOT_ID);
  }

  private void setRankingFields(Object tasklet, String periodType) {
    setField(tasklet, "periodTypeValue", periodType);
    setField(tasklet, "aggregatedAtValue", AGGREGATED_AT.toString());
    setField(tasklet, "snapshotIdValue", SNAPSHOT_ID.toString());
  }

  private void setField(Object target, String name, Object value) {
    ReflectionTestUtils.setField(target, name, value);
  }

  private ChunkContext chunkContext() {
    JobExecution jobExecution = new JobExecution(1L);
    StepExecution stepExecution = new StepExecution("dashboardStep", jobExecution);
    return new ChunkContext(new StepContext(stepExecution));
  }
}
