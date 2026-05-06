package com.codeit.mission.deokhugam.dashboard.batch;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.dashboard.exceptions.DashboardBatchJobFailException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

class DashboardBatchSchedulerTest {

  @Test
  @DisplayName("runDashboardAggregation runs every dashboard domain and period job")
  void runDashboardAggregation_runsAllJobs() throws Exception {
    JobLauncher jobLauncher = mock(JobLauncher.class);
    Job powerUserJob = mock(Job.class);
    Job popularReviewJob = mock(Job.class);
    Job popularBookJob = mock(Job.class);
    JobExecution execution = new JobExecution(1L);
    execution.setStatus(BatchStatus.COMPLETED);
    when(jobLauncher.run(any(Job.class), any(JobParameters.class))).thenReturn(execution);
    DashboardBatchScheduler scheduler =
        new DashboardBatchScheduler(jobLauncher, powerUserJob, popularReviewJob, popularBookJob);

    scheduler.runDashboardAggregation();

    verify(jobLauncher, times(4)).run(eq(powerUserJob), any(JobParameters.class));
    verify(jobLauncher, times(4)).run(eq(popularReviewJob), any(JobParameters.class));
    verify(jobLauncher, times(4)).run(eq(popularBookJob), any(JobParameters.class));
  }

  @Test
  @DisplayName("runDashboardAggregation wraps job launcher failures")
  void runDashboardAggregation_wrapsFailure() throws Exception {
    JobLauncher jobLauncher = mock(JobLauncher.class);
    Job powerUserJob = mock(Job.class);
    Job popularReviewJob = mock(Job.class);
    Job popularBookJob = mock(Job.class);
    JobExecution completed = new JobExecution(1L);
    completed.setStatus(BatchStatus.COMPLETED);
    when(jobLauncher.run(eq(popularReviewJob), any(JobParameters.class))).thenReturn(completed);
    when(jobLauncher.run(eq(popularBookJob), any(JobParameters.class))).thenReturn(completed);

    when(jobLauncher.run(eq(powerUserJob), any(JobParameters.class)))
        .thenThrow(new IllegalStateException("job failed"));
    DashboardBatchScheduler scheduler =
        new DashboardBatchScheduler(jobLauncher, powerUserJob, popularReviewJob, popularBookJob);

    assertThatThrownBy(scheduler::runDashboardAggregation)
        .isInstanceOf(DashboardBatchJobFailException.class);
  }
}
