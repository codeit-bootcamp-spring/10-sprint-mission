package com.codeit.mission.deokhugam.dashboard.popularreviews.batch;

import com.codeit.mission.deokhugam.dashboard.batch.DashboardAggregationJobListener;
import com.codeit.mission.deokhugam.dashboard.popularreviews.batch.tasklet.RankPopularReviewTasklet;
import com.codeit.mission.deokhugam.dashboard.popularreviews.entity.PopularReview;
import com.codeit.mission.deokhugam.review.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PopularReviewBatchConfig {
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job popularReviewAggregationJob(
      Step createNewSnapshotStep,
      Step aggregatePopularReviewsStep,
      Step rankPopularReviewsStep,
      Step publishSnapshotStep,
      Step cleanupOldSnapshotsStep,
      DashboardAggregationJobListener listener
  ) {
    return new JobBuilder("popularReviewAggregationJob", jobRepository)
        .listener(listener)
        .start(createNewSnapshotStep)
        .next(aggregatePopularReviewsStep)
        .next(rankPopularReviewsStep)
        .next(publishSnapshotStep)
        .next(cleanupOldSnapshotsStep)
        .build();
  }

  @Bean
  public Step aggregatePopularReviewsStep(
      @Qualifier("reviewReader") JpaPagingItemReader<Review> itemReader,
      @Qualifier("reviewProcessor") PopularReviewItemProcessor processor,
      @Qualifier("reviewWriter") JpaItemWriter<PopularReview> writer
  ){
    return new StepBuilder("aggregatePopularReviewsStep", jobRepository)
        .<Review, PopularReview>chunk(100,transactionManager)
        .reader(itemReader)
        .processor(processor)
        .writer(writer)
        .build();
  }

  @Bean
  public Step rankPopularReviewsStep(RankPopularReviewTasklet rankPopularReviewTasklet){
    return new StepBuilder("rankPopularReviewsStep", jobRepository)
        .tasklet(rankPopularReviewTasklet, transactionManager)
        .build();
  }
}
