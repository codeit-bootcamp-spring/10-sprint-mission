package com.codeit.mission.deokhugam.dashboard.snapshot;

import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregateSnapshotRepository extends JpaRepository<AggregateSnapshot, UUID> {
  Optional<AggregateSnapshot> findBySnapshotId(UUID snapshotId);

  Optional<AggregateSnapshot> findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
      DomainType domainType,
      PeriodType periodType,
      StagingType stagingType
  );

  // 도메인 타입, 기간, 스테이징 타입에 해당하는 스냅샷들을 반환한다.
  List<AggregateSnapshot> findByStagingType(StagingType stagingType);

  List<AggregateSnapshot> findByDomainTypeAndPeriodTypeAndStagingTypeInOrderByCreatedAtDesc(
      DomainType domainType,
      PeriodType periodType,
      List<StagingType> stagingType
  );
}
