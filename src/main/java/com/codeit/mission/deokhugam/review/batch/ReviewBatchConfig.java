package com.codeit.mission.deokhugam.review.batch;

import com.codeit.mission.deokhugam.comment.repository.CommentRepository;
import com.codeit.mission.deokhugam.notification.repository.NotificationRepository;
import com.codeit.mission.deokhugam.review.entity.ReviewStatus;
import com.codeit.mission.deokhugam.review.repository.ReviewLikeRepository;
import com.codeit.mission.deokhugam.review.repository.ReviewRepository;
import jakarta.persistence.EntityManagerFactory;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/*
    ReviewBatchConfig
    --------------------
    리뷰 물리 삭제 (Hard Delete)에 활용할 리뷰 전용 Spring Batch 설정
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class ReviewBatchConfig {

  private final JobRepository jobRepository;                      // Batch 결과 저장소
  private final PlatformTransactionManager transactionManager;    // 트랜잭션 관리 매니저
  private final EntityManagerFactory entityManagerFactory;        // JPA - DB 간 소통 객체

  private final ReviewRepository reviewRepository;
  private final ReviewLikeRepository reviewLikeRepository;
  private final CommentRepository commentRepository;
  private final NotificationRepository notificationRepository;

  private static final int CHUNK_SIZE = 100;                      // 한 번에 처리할 데이터 개수

  // [Job 설정]: 리뷰 물리 삭제 Job 생성
  @Bean
  public Job reviewHardDeleteJob(Step reviewHardDeleteStep) {
    return new JobBuilder("reviewHardDeleteJob", jobRepository)
        .start(reviewHardDeleteStep)
        // Job 시작 전, 사전 작업을 수행할 객체
        .listener(reviewHardDeleteJobListener())
        .build();
  }

  // [Job Listener 설정]: Job 실행 직전에 기준 시간 (threshold) 계산하여 컨텍스트에 저장
  @Bean
  public JobExecutionListener reviewHardDeleteJobListener() {
    return new JobExecutionListener() {     // 익명 클래스 구현

      @Override
      // 사전 작업: 삭제 기준 날짜 지정
      public void beforeJob(@NonNull JobExecution jobExecution) {
        Instant threshold = Instant.now().minus(1, ChronoUnit.DAYS);
        jobExecution.getExecutionContext().put("threshold", threshold.toString());
      }
    };
  }

  // [Step 설정]: Job 내부에서 수행될 읽기 (Reader) / 쓰기 (Writer) 조합
  @Bean
  public Step reviewHardDeleteStep(
      JpaCursorItemReader<UUID> reviewHardDeleteReader,
      ItemWriter<UUID> reviewHardDeleteWriter) {
    return new StepBuilder("reviewHardDeleteStep", jobRepository)
        // 리뷰 100개를 하나의 트랜잭션으로 처리
        .<UUID, UUID>chunk(CHUNK_SIZE, transactionManager)
        .reader(reviewHardDeleteReader)       // 리뷰 읽어오기
        .writer(reviewHardDeleteWriter)       // 리뷰 삭제하기
        .build();
  }

  // [Reader 설정]: 논리적으로 삭제된 지 하루가 경과한 리뷰 읽기
  @Bean
  @StepScope
  public JpaCursorItemReader<UUID> reviewHardDeleteReader(
      // JobExecutionListener가 수행한 삭제 날짜 기준
      @Value("#{jobExecutionContext['threshold'] ?: T(java.time.Instant).now().minus(1, T(java.time.temporal.ChronoUnit).DAYS).toString()}") String thresholdStr) {
    // 1. 삭제 기준 날짜 설정
    Instant threshold = Instant.parse(thresholdStr);

    // 2. 읽기 (Reader) 객체 반환
    return new JpaCursorItemReaderBuilder<UUID>()
        .name("reviewHardDeleteReader")
        .entityManagerFactory(entityManagerFactory)
        // 안전한 데이터 삭제를 위해 cursor 방식으로 구현하여, 쿼리문 직접 작성
        .queryString(
            "SELECT review.id FROM Review review WHERE review.status = :status AND review.updatedAt <= :threshold")
        .parameterValues(Map.of(
            "status", ReviewStatus.DELETED,
            "threshold", threshold
        ))
        .build();
  }

  // [Writer 설정]: 읽어온 리뷰를 물리적으로 삭제
  @Bean
  @StepScope
  public ItemWriter<UUID> reviewHardDeleteWriter() {
    return chunk -> {
      // 1. 지워야 할 리뷰 ID 목록 조회
      List<UUID> reviewIds = (List<UUID>) chunk.getItems();

      // 2. 지워야 할 리뷰가 없는 경우, 종료
      if (reviewIds.isEmpty()) {
        return;
      }

      // 3. 삭제 대상인 리뷰와 연관된 데이터 삭제 (리뷰 좋아요, 댓글, 알림)
      commentRepository.deleteByReviewIdIn(reviewIds);
      reviewLikeRepository.deleteByReviewIdIn(reviewIds);
      notificationRepository.deleteByReviewIdIn(reviewIds);

      // 4. 리뷰 물리 삭제
      reviewRepository.deleteAllByIdInBatch(reviewIds);

      // 5. 로그 기록
      log.info("[REVIEW_BATCH] 보관 기간 만료된 리뷰 {}개 물리 삭제 진행", chunk.size());
    };
  }
}
