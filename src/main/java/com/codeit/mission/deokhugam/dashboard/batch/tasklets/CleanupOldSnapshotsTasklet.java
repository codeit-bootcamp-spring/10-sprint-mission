package com.codeit.mission.deokhugam.dashboard.batch.tasklets;

import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotService;
import com.codeit.mission.deokhugam.dashboard.util.JobParameterUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@StepScope
@RequiredArgsConstructor
@Slf4j
public class CleanupOldSnapshotsTasklet implements Tasklet {

  private final AggregateSnapshotService aggregateSnapshotService;

  @Value("#{jobParameters['periodType']}")
  private String periodTypeValue;

  @Value("#{jobExecutionContext['domainType']}")
  private String domainTypeValue;

  @Value("${dashboard.snapshot.cleanup.keep-count:2}")
  private int keepCount;

  @Override
  public @Nullable RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
    DomainType domainType = getDomainType();
    PeriodType periodType = getPeriodType();

    log.info("[DASHBOARD_SNAPSHOT_CLEANUP_START] domainType={}, periodType={}, keepCount={}",
        domainType, periodType, keepCount);
    aggregateSnapshotService.cleanupOldSnapshots(domainType, periodType, keepCount);
    log.info("[DASHBOARD_SNAPSHOT_CLEANUP_DONE] domainType={}, periodType={}, keepCount={}",
        domainType, periodType, keepCount);

    return RepeatStatus.FINISHED;
  }

  private DomainType getDomainType() {
    return JobParameterUtils.parseEnum("domainType", domainTypeValue, DomainType.class);
  }

  private PeriodType getPeriodType() {
    return JobParameterUtils.parseEnum("periodType", periodTypeValue, PeriodType.class);
  }

}
