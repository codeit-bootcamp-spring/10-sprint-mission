package com.codeit.mission.deokhugam.review.batch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReviewBatchSchedulerTest {

  @Mock
  private JobLauncher jobLauncher;

  @Mock
  private Job reviewHardDeleteJob;

  @InjectMocks
  private ReviewBatchScheduler reviewBatchScheduler;

  /*
      리뷰 배치 스케줄러
   */

  @Test
  @DisplayName("배치 상태 검증: JobExecution 상태가 COMPLETED일 때 정상 처리")
  void run_review_hard_delete_job_success() throws Exception {
    // given
    JobExecution mockExecution = new JobExecution(1L);
    mockExecution.setStatus(BatchStatus.COMPLETED);

    given(jobLauncher.run(eq(reviewHardDeleteJob), any(JobParameters.class)))
        .willReturn(mockExecution);

    // when
    reviewBatchScheduler.runReviewHardDeleteJob();

    // then
    verify(jobLauncher, times(1)).run(eq(reviewHardDeleteJob), any(JobParameters.class));
  }

  @Test
  @DisplayName("배치 상태 검증: JobExecution 상태가 FAILED일 때 에러 로그 기록")
  void run_review_hard_delete_job_status_failed() throws Exception {
    // given
    JobExecution mockExecution = new JobExecution(2L);
    mockExecution.setStatus(BatchStatus.FAILED);

    given(jobLauncher.run(eq(reviewHardDeleteJob), any(JobParameters.class)))
        .willReturn(mockExecution);

    // when
    reviewBatchScheduler.runReviewHardDeleteJob();

    // then
    verify(jobLauncher, times(1)).run(eq(reviewHardDeleteJob), any(JobParameters.class));
  }

  @Test
  @DisplayName("배치 상태 검증: JobExecution 상태가 기타일 때 상태 로그 기록")
  void run_review_hard_delete_job_status_others() throws Exception {
    // given
    JobExecution mockExecution = new JobExecution(3L);
    mockExecution.setStatus(BatchStatus.STOPPED);

    given(jobLauncher.run(eq(reviewHardDeleteJob), any(JobParameters.class)))
        .willReturn(mockExecution);

    // when
    reviewBatchScheduler.runReviewHardDeleteJob();

    // then
    verify(jobLauncher, times(1)).run(eq(reviewHardDeleteJob), any(JobParameters.class));
  }

  @Test
  @DisplayName("스케줄러 실행 중 예외가 발생해도 서버 종료 없이 예외를 처리")
  void run_review_hard_delete_job_exception_handled() throws Exception {
    // given
    given(jobLauncher.run(eq(reviewHardDeleteJob), any(JobParameters.class)))
        .willThrow(new RuntimeException("Job execution failed."));

    // when & then
    assertDoesNotThrow(() -> reviewBatchScheduler.runReviewHardDeleteJob());

    verify(jobLauncher, times(1)).run(eq(reviewHardDeleteJob), any(JobParameters.class));
  }
}