package com.codeit.mission.deokhugam.dashboard.batch.tasklets.steps;

import com.codeit.mission.deokhugam.dashboard.batch.tasklets.CleanupOldSnapshotsTasklet;
import com.codeit.mission.deokhugam.dashboard.batch.tasklets.CreateNewSnapshotTasklet;
import com.codeit.mission.deokhugam.dashboard.batch.tasklets.PublishSnapshotTasklet;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
// 도메인 별 Job 수행 시 공용으로 사용되는 Step을 설정하고 Bean으로 만드는 Config
public class DashboardCommonStepConfig {

  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  // 스냅샷을 생성하는 Step을 설정한다.
  @Bean
  public Step createNewSnapshotStep(CreateNewSnapshotTasklet createNewSnapshotTasklet) {
    return new StepBuilder("createNewSnapshotStep", jobRepository)
        .tasklet(createNewSnapshotTasklet, transactionManager)
        .build();
  }

  // 스냅샷을 Publish 하는 Step을 설정한다.
  @Bean
  public Step publishSnapshotStep(PublishSnapshotTasklet publishSnapshotTasklet) {
    return new StepBuilder("publishSnapshotStep", jobRepository)
        .tasklet(publishSnapshotTasklet, transactionManager)
        .build();
  }

  @Bean
  public Step cleanupOldSnapshotsStep(CleanupOldSnapshotsTasklet cleanupOldSnapshotsTasklet){
    return new StepBuilder("cleanupOldSnapshotsStep", jobRepository)
        .tasklet(cleanupOldSnapshotsTasklet, transactionManager)
        .build();
  }

}
