package com.codeit.mission.deokhugam.dashboard.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.codeit.mission.deokhugam.config.JpaAuditingConfig;
import com.codeit.mission.deokhugam.config.QuerydslConfig;
import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({JpaAuditingConfig.class, QuerydslConfig.class})
class AggregateSnapshotRepositoryTest {

  @Autowired
  private AggregateSnapshotRepository aggregateSnapshotRepository;

  @Autowired
  private EntityManager em;

  @Test
  @DisplayName("finds matching snapshots by staging types ordered by createdAt desc")
  void findByDomainTypeAndPeriodTypeAndStagingTypeInOrderByCreatedAtDesc_filtersAndOrders() {
    AggregateSnapshot oldPublished = persistSnapshot(
        DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.PUBLISHED,
        Instant.parse("2026-04-21T00:00:00Z"));
    AggregateSnapshot newArchived = persistSnapshot(
        DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.ARCHIVED,
        Instant.parse("2026-04-22T00:00:00Z"));
    AggregateSnapshot staging = persistSnapshot(
        DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.STAGING,
        Instant.parse("2026-04-23T00:00:00Z"));
    AggregateSnapshot otherDomain = persistSnapshot(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED,
        Instant.parse("2026-04-24T00:00:00Z"));
    AggregateSnapshot otherPeriod = persistSnapshot(
        DomainType.POPULAR_REVIEW, PeriodType.DAILY, StagingType.PUBLISHED,
        Instant.parse("2026-04-25T00:00:00Z"));

    em.flush();
    updateCreatedAt(oldPublished.getSnapshotId(), Instant.parse("2026-04-21T00:00:00Z"));
    updateCreatedAt(newArchived.getSnapshotId(), Instant.parse("2026-04-22T00:00:00Z"));
    updateCreatedAt(staging.getSnapshotId(), Instant.parse("2026-04-23T00:00:00Z"));
    updateCreatedAt(otherDomain.getSnapshotId(), Instant.parse("2026-04-24T00:00:00Z"));
    updateCreatedAt(otherPeriod.getSnapshotId(), Instant.parse("2026-04-25T00:00:00Z"));
    em.clear();

    List<AggregateSnapshot> result =
        aggregateSnapshotRepository.findByDomainTypeAndPeriodTypeAndStagingTypeInOrderByCreatedAtDesc(
            DomainType.POPULAR_REVIEW,
            PeriodType.WEEKLY,
            List.of(StagingType.PUBLISHED, StagingType.ARCHIVED));

    assertEquals(2, result.size());
    assertEquals(newArchived.getSnapshotId(), result.get(0).getSnapshotId());
    assertEquals(oldPublished.getSnapshotId(), result.get(1).getSnapshotId());
  }

  private AggregateSnapshot persistSnapshot(
      DomainType domainType,
      PeriodType periodType,
      StagingType stagingType,
      Instant aggregatedAt) {
    AggregateSnapshot snapshot = AggregateSnapshot.builder()
        .snapshotId(UUID.randomUUID())
        .domainType(domainType)
        .periodType(periodType)
        .stagingType(stagingType)
        .aggregatedAt(aggregatedAt)
        .build();
    em.persist(snapshot);
    return snapshot;
  }

  private void updateCreatedAt(UUID snapshotId, Instant createdAt) {
    int updatedRows =
        em.createQuery(
                "update AggregateSnapshot s set s.createdAt = :createdAt where s.snapshotId = :snapshotId")
            .setParameter("createdAt", createdAt)
            .setParameter("snapshotId", snapshotId)
            .executeUpdate();

    assertEquals(1, updatedRows);
  }
}
