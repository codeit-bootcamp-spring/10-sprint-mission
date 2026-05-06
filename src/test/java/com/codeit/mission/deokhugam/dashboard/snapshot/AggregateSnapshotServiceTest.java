package com.codeit.mission.deokhugam.dashboard.snapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import com.codeit.mission.deokhugam.dashboard.exceptions.DomainTypeNotEqualException;
import com.codeit.mission.deokhugam.dashboard.exceptions.InvalidKeepCountException;
import com.codeit.mission.deokhugam.dashboard.exceptions.SnapshotNotFoundException;
import com.codeit.mission.deokhugam.dashboard.exceptions.SnapshotNotStagedPublishException;
import com.codeit.mission.deokhugam.dashboard.popularbooks.repository.PopularBookRepository;
import com.codeit.mission.deokhugam.dashboard.popularreviews.repository.PopularReviewRepository;
import com.codeit.mission.deokhugam.dashboard.powerusers.repository.PowerUserRepository;
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
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class AggregateSnapshotServiceTest {

  private static final UUID SNAPSHOT_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final Instant AGGREGATED_AT = Instant.parse("2026-04-27T00:00:00Z");

  @Mock
  private AggregateSnapshotRepository snapshotRepository;

  @Mock
  private PopularReviewRepository popularReviewRepository;

  @Mock
  private PopularBookRepository popularBookRepository;

  @Mock
  private PowerUserRepository powerUserRepository;

  @Mock
  private CacheManager cacheManager;

  @InjectMocks
  private AggregateSnapshotService aggregateSnapshotService;

  @Test
  @DisplayName("createStagingSnapshot archives existing staging snapshot and saves a new one")
  void createStagingSnapshot_archivesExistingStagingSnapshot() {
    AggregateSnapshot oldStaging = snapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.STAGING, UUID.randomUUID());
    when(snapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.STAGING))
        .thenReturn(Optional.of(oldStaging));
    when(snapshotRepository.save(any(AggregateSnapshot.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    AggregateSnapshot result = aggregateSnapshotService.createStagingSnapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, AGGREGATED_AT);

    assertThat(oldStaging.getStagingType()).isEqualTo(StagingType.ARCHIVED);
    assertThat(result.getDomainType()).isEqualTo(DomainType.POPULAR_BOOK);
    assertThat(result.getPeriodType()).isEqualTo(PeriodType.WEEKLY);
    assertThat(result.getAggregatedAt()).isEqualTo(AGGREGATED_AT);
    assertThat(result.getStagingType()).isEqualTo(StagingType.STAGING);
    verify(snapshotRepository).save(result);
  }

  @Test
  @DisplayName("publishSnapshot throws when snapshot does not exist")
  void publishSnapshot_snapshotNotFound() {
    when(snapshotRepository.findBySnapshotId(SNAPSHOT_ID)).thenReturn(Optional.empty());

    assertThatThrownBy(
        () -> aggregateSnapshotService.publishSnapshot(DomainType.POPULAR_BOOK, SNAPSHOT_ID))
        .isInstanceOf(SnapshotNotFoundException.class);

    verify(snapshotRepository, never())
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            any(), any(), any());
  }

  @Test
  @DisplayName("publishSnapshot throws when requested domain does not match snapshot domain")
  void publishSnapshot_domainMismatch() {
    AggregateSnapshot snapshot = snapshot(
        DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.STAGING, SNAPSHOT_ID);
    when(snapshotRepository.findBySnapshotId(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

    assertThatThrownBy(
        () -> aggregateSnapshotService.publishSnapshot(DomainType.POPULAR_BOOK, SNAPSHOT_ID))
        .isInstanceOf(DomainTypeNotEqualException.class);

    assertThat(snapshot.getStagingType()).isEqualTo(StagingType.STAGING);
  }

  @Test
  @DisplayName("publishSnapshot throws when snapshot is not staging")
  void publishSnapshot_notStaging() {
    AggregateSnapshot snapshot = snapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED, SNAPSHOT_ID);
    when(snapshotRepository.findBySnapshotId(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

    assertThatThrownBy(
        () -> aggregateSnapshotService.publishSnapshot(DomainType.POPULAR_BOOK, SNAPSHOT_ID))
        .isInstanceOf(SnapshotNotStagedPublishException.class);
  }

  @Test
  @DisplayName("failSnapshot marks staging snapshot as failed")
  void failSnapshot_stagingSnapshot() {
    AggregateSnapshot snapshot = snapshot(
        DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.STAGING, SNAPSHOT_ID);
    when(snapshotRepository.findBySnapshotId(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

    aggregateSnapshotService.failSnapshot(SNAPSHOT_ID);

    assertThat(snapshot.getStagingType()).isEqualTo(StagingType.FAILED);
  }

  @Test
  @DisplayName("failSnapshot ignores non-staging snapshot")
  void failSnapshot_nonStagingSnapshot() {
    AggregateSnapshot snapshot = snapshot(
        DomainType.POWER_USER, PeriodType.WEEKLY, StagingType.PUBLISHED, SNAPSHOT_ID);
    when(snapshotRepository.findBySnapshotId(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));

    aggregateSnapshotService.failSnapshot(SNAPSHOT_ID);

    assertThat(snapshot.getStagingType()).isEqualTo(StagingType.PUBLISHED);
  }

  @Test
  @DisplayName("cleanupOldSnapshots rejects keepCount less than two")
  void cleanupOldSnapshots_invalidKeepCount() {
    assertThatThrownBy(
        () -> aggregateSnapshotService.cleanupOldSnapshots(
            DomainType.POPULAR_BOOK, PeriodType.WEEKLY, 1))
        .isInstanceOf(InvalidKeepCountException.class);

    verifyNoInteractions(popularBookRepository, popularReviewRepository, powerUserRepository);
  }

  @Test
  @DisplayName("cleanupOldSnapshots deletes old popular book rankings and snapshots")
  void cleanupOldSnapshots_deletesPopularBookRows() {
    AggregateSnapshot keptPublished = snapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED, UUID.randomUUID());
    AggregateSnapshot keptArchived = snapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.ARCHIVED, UUID.randomUUID());
    AggregateSnapshot target = snapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.ARCHIVED, SNAPSHOT_ID);
    when(snapshotRepository.findByDomainTypeAndPeriodTypeAndStagingTypeInOrderByCreatedAtDesc(
        DomainType.POPULAR_BOOK,
        PeriodType.WEEKLY,
        List.of(StagingType.PUBLISHED, StagingType.ARCHIVED)))
        .thenReturn(List.of(keptPublished, keptArchived, target));
    when(snapshotRepository.findByStagingType(StagingType.FAILED)).thenReturn(List.of());

    aggregateSnapshotService.cleanupOldSnapshots(DomainType.POPULAR_BOOK, PeriodType.WEEKLY, 2);

    verify(popularBookRepository).deleteBySnapshotIdIn(List.of(SNAPSHOT_ID));
    verify(popularReviewRepository, never()).deleteBySnapshotIdIn(any());
    verify(powerUserRepository, never()).deleteBySnapshotIdIn(any());
    verify(snapshotRepository).deleteAllInBatch(List.of(target));
  }

  @Test
  @DisplayName("cleanupFailedSnapshots deletes failed rows for each dashboard domain")
  void cleanupFailedSnapshots_deletesRowsByDomain() {
    AggregateSnapshot failedBook =
        snapshot(DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.FAILED, UUID.randomUUID());
    AggregateSnapshot failedReview =
        snapshot(DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.FAILED, UUID.randomUUID());
    AggregateSnapshot failedPowerUser =
        snapshot(DomainType.POWER_USER, PeriodType.WEEKLY, StagingType.FAILED, UUID.randomUUID());
    when(snapshotRepository.findByStagingType(StagingType.FAILED))
        .thenReturn(List.of(failedBook, failedReview, failedPowerUser));

    aggregateSnapshotService.cleanupFailedSnapshots();

    verify(popularBookRepository).deleteBySnapshotIdIn(List.of(failedBook.getSnapshotId()));
    verify(popularReviewRepository).deleteBySnapshotIdIn(List.of(failedReview.getSnapshotId()));
    verify(powerUserRepository).deleteBySnapshotIdIn(List.of(failedPowerUser.getSnapshotId()));
    verify(snapshotRepository).deleteAllInBatch(List.of(failedBook, failedReview, failedPowerUser));
  }

  private AggregateSnapshot snapshot(
      DomainType domainType,
      PeriodType periodType,
      StagingType stagingType,
      UUID snapshotId
  ) {
    return AggregateSnapshot.builder()
        .snapshotId(snapshotId)
        .domainType(domainType)
        .periodType(periodType)
        .stagingType(stagingType)
        .aggregatedAt(AGGREGATED_AT)
        .build();
  }
}
