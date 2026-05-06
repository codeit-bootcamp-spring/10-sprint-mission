package com.codeit.mission.deokhugam.dashboard.batch;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotService;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.item.ExecutionContext;

@ExtendWith(MockitoExtension.class)
class DashboardAggregationJobListenerTest {

  @Mock
  private AggregateSnapshotService aggregateSnapshotService;

  @InjectMocks
  private DashboardAggregationJobListener listener;

  @Test
  @DisplayName("afterJob ignores non-failed jobs")
  void afterJob_nonFailedJob() {
    JobExecution jobExecution = jobExecution(BatchStatus.COMPLETED, null);

    listener.afterJob(jobExecution);

    verify(aggregateSnapshotService, never()).failSnapshot(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("afterJob marks snapshot as failed when failed job has snapshot id")
  void afterJob_failedJobWithSnapshotId() {
    UUID snapshotId = UUID.randomUUID();
    JobExecution jobExecution = jobExecution(BatchStatus.FAILED, snapshotId.toString());

    listener.afterJob(jobExecution);

    verify(aggregateSnapshotService).failSnapshot(snapshotId);
  }

  @Test
  @DisplayName("afterJob skips failed job without snapshot id")
  void afterJob_failedJobWithoutSnapshotId() {
    JobExecution jobExecution = jobExecution(BatchStatus.FAILED, " ");

    listener.afterJob(jobExecution);

    verify(aggregateSnapshotService, never()).failSnapshot(org.mockito.ArgumentMatchers.any());
  }

  @Test
  @DisplayName("afterJob skips invalid snapshot id")
  void afterJob_invalidSnapshotId() {
    JobExecution jobExecution = jobExecution(BatchStatus.FAILED, "invalid-snapshot-id");

    listener.afterJob(jobExecution);

    verify(aggregateSnapshotService, never()).failSnapshot(org.mockito.ArgumentMatchers.any());
  }

  private JobExecution jobExecution(BatchStatus status, String snapshotId) {
    JobInstance jobInstance = org.mockito.Mockito.mock(JobInstance.class);
    when(jobInstance.getJobName()).thenReturn("dashboardJob");

    JobExecution jobExecution = new JobExecution(jobInstance, 1L, null);
    jobExecution.setStatus(status);
    jobExecution.setExitStatus(status == BatchStatus.FAILED ? ExitStatus.FAILED : ExitStatus.COMPLETED);

    ExecutionContext context = jobExecution.getExecutionContext();
    if (snapshotId != null) {
      context.putString("snapshotId", snapshotId);
    }

    return jobExecution;
  }
}
