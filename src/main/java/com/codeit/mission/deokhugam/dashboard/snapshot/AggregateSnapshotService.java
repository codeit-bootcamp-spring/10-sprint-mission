package com.codeit.mission.deokhugam.dashboard.snapshot;

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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
public class AggregateSnapshotService {

  private final AggregateSnapshotRepository snapshotRepository;
  private final PopularReviewRepository popularReviewRepository;
  private final PopularBookRepository popularBookRepository;
  private final PowerUserRepository powerUserRepository;


  private final CacheManager cacheManager; // 캐시 매니저 주입

  // 새로운 스냅샷을 생성하는 서비스 -> Batch Job의 CreateNewSnapshot
  @Transactional
  public AggregateSnapshot createStagingSnapshot(
      DomainType domainType,
      PeriodType periodType,
      Instant aggregatedAt
  ) {
    snapshotRepository
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            domainType, periodType, StagingType.STAGING)
        .ifPresent(AggregateSnapshot::archive);

    AggregateSnapshot snapshot = AggregateSnapshot.builder()
        .snapshotId(UUID.randomUUID())
        .periodType(periodType)
        .domainType(domainType)
        .aggregatedAt(aggregatedAt)
        .stagingType(StagingType.STAGING)
        .build();

    return snapshotRepository.save(snapshot);
  }

  @Transactional
  public void publishSnapshot(DomainType domainType, UUID snapshotId) {
    // 조회하고자 하는 스냅샷의 존재 여부를 확인하고 가져옴.
    AggregateSnapshot newSnapshot = snapshotRepository.findBySnapshotId(snapshotId)
        .orElseThrow(SnapshotNotFoundException::new);

    // 스냅샷의 도메인 타입이 서로 일치하는지 확인
    if (!Objects.equals(newSnapshot.getDomainType(), domainType)) {
      throw new DomainTypeNotEqualException(Map.of(
          "newDomainType", newSnapshot.getDomainType(),
          "existingDomainType", domainType));
    }

    // 스냅샷의 상태를 확인 (스테이징 상태여야만 publish 가능)
    if (newSnapshot.getStagingType() != StagingType.STAGING) {
      throw new SnapshotNotStagedPublishException(Map.of(
          "snapshotId", newSnapshot.getSnapshotId(),
          "stagingType", newSnapshot.getStagingType()));
    }

    // 이전에 PUBLISHED 스냅샷을 ARCHIVED(보존) 상태로 바꾼다.
    snapshotRepository
        .findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
            domainType, newSnapshot.getPeriodType(), StagingType.PUBLISHED)
        .filter(oldSnapshot -> !oldSnapshot.getSnapshotId().equals(snapshotId))
        .ifPresent(AggregateSnapshot::archive);

    // 해당 스냅샷을 PUBLISHED로 바꾼다.
    newSnapshot.publish();

    // 트랜잭션 내 캐시 무효화 시 일관성 문제 가능성을 해소하기 위해
    // TransactionSynchronizationManager를 사용하여
    // 커밋 후 콜백으로 처리
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization(){
          @Override
          public void afterCommit(){
            evictDashboardCache(domainType);
          }
        }
    );
  }

  // 스냅샷을 새로 Publish 하고나서, Redis 캐시에 남아있는 이전의 값들을 정리하는 메서드
  private void evictDashboardCache(DomainType domainType) {
    // 도메인 타입에 맞는 캐시를 (캐시 이름을) 대입한다.
    String cacheName = switch (domainType) {
      case POPULAR_BOOK -> "popularBooks";
      case POPULAR_REVIEW -> "popularReviews";
      case POWER_USER -> "powerUsers";
    };

    // cacheName에 해당되는 캐시를 구하고,
    Cache cache = cacheManager.getCache(cacheName);

    // cache 안에 이전 값들이 남아있으면 clear한다.
    if (cache != null) {
      cache.clear();
    }
  }

  @Transactional
  public void failSnapshot(UUID snapshotId) {
    snapshotRepository.findBySnapshotId(snapshotId)
        .filter(snapshot -> snapshot.getStagingType() == StagingType.STAGING)
        .ifPresent(AggregateSnapshot::fail);
  }

  // 오래된 스냅샷들을 정리한다.
  @Transactional
  public void cleanupOldSnapshots(DomainType domainType, PeriodType periodType, int keepCount) {
    // keepCount가 2 이하이면 PUBLISHED 스냅샷도 삭제될 수 있기 때문에 검증을 둔다.
    if (keepCount < 2) {
      throw new InvalidKeepCountException(keepCount);
    }

    // PUBLISHED, ARCHIVED 스냅샷만 createdAt 내림차순으로 가져온다.
    List<AggregateSnapshot> snapshots =
        snapshotRepository.findByDomainTypeAndPeriodTypeAndStagingTypeInOrderByCreatedAtDesc(
            domainType,
            periodType,
            List.of(StagingType.PUBLISHED, StagingType.ARCHIVED)
        );

    // keepCount 만큼 남겨두고 그 뒤의 스냅샷들을 삭제 대상으로 삼는다.
    List<AggregateSnapshot> targets = snapshots.stream().skip(keepCount).toList();

    if (!targets.isEmpty()) {
      // 삭제 대상 스냅샷들의 id를 추출한다.
      List<UUID> snapshotIds = targets.stream()
          .map(AggregateSnapshot::getSnapshotId)
          .toList();

      // 지정한 스냅샷들의 도메인별 집계 row를 먼저 삭제한다.
      deleteAggregateRows(domainType, snapshotIds);
      snapshotRepository.deleteAllInBatch(targets);
    }

    cleanupFailedSnapshots();
  }

  // FAILED 스냅샷들을 정리한다.
  @Transactional
  public void cleanupFailedSnapshots() {
    // FAILED 스냅샷들을 모두 가져온다.
    List<AggregateSnapshot> failedSnapshots = snapshotRepository.findByStagingType(
        StagingType.FAILED);

    // FAILED 스냅샷이 없으면 리턴
    if (failedSnapshots.isEmpty()) {
      return;
    }

    // 각 도메인 별로 스냅샷의 ID를 리스트화한다.
    for (DomainType domainType : DomainType.values()) {
      List<UUID> snapshotIds = failedSnapshots.stream()
          .filter(snapshot -> snapshot.getDomainType() == domainType)
          .map(AggregateSnapshot::getSnapshotId)
          .toList();

      if (!snapshotIds.isEmpty()) {
        // 레포지토리 내의 도메인별 집계 row 삭제
        deleteAggregateRows(domainType, snapshotIds);
      }
    }

    // aggregate_snapshot 테이블의 FAILED snapshot row 자체를 삭제
    snapshotRepository.deleteAllInBatch(failedSnapshots);
  }

  // 각 도메인 별 레포지토리에서 오래된 스냅샷의 엔티티들을 삭제
  private void deleteAggregateRows(DomainType domainType, List<UUID> snapshotIds) {
    Runnable action = switch (domainType) {
      case POPULAR_BOOK -> () -> popularBookRepository.deleteBySnapshotIdIn(snapshotIds);
      case POPULAR_REVIEW -> () -> popularReviewRepository.deleteBySnapshotIdIn(snapshotIds);
      case POWER_USER -> () -> powerUserRepository.deleteBySnapshotIdIn(snapshotIds);
    };
    action.run();
  }
}
