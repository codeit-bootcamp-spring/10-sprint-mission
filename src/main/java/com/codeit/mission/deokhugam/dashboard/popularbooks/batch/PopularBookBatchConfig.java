package com.codeit.mission.deokhugam.dashboard.popularbooks.batch;

import com.codeit.mission.deokhugam.book.entity.Book;
import com.codeit.mission.deokhugam.dashboard.batch.DashboardAggregationJobListener;
import com.codeit.mission.deokhugam.dashboard.popularbooks.batch.tasklet.PopularBookItemProcessor;
import com.codeit.mission.deokhugam.dashboard.popularbooks.batch.tasklet.RankPopularBookTasklet;
import com.codeit.mission.deokhugam.dashboard.popularbooks.entity.PopularBook;
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
public class PopularBookBatchConfig {
  private final JobRepository jobRepository;
  private final PlatformTransactionManager transactionManager;

  @Bean
  public Job popularBookAggregationJob(
      Step createNewSnapshotStep,
      Step aggregatePopularBooksStep,
      Step rankPopularBooksStep,
      Step publishSnapshotStep,
      Step cleanupOldSnapshotsStep,
      DashboardAggregationJobListener listener){
    return new JobBuilder("popularBookAggregationJob", jobRepository)
        .listener(listener)
        .start(createNewSnapshotStep)
        .next(aggregatePopularBooksStep)
        .next(rankPopularBooksStep)
        .next(publishSnapshotStep)
        .next(cleanupOldSnapshotsStep)
        .build();
  }

  @Bean
  public Step aggregatePopularBooksStep(
      @Qualifier("bookReader") JpaPagingItemReader<Book> itemReader,
      @Qualifier("bookProcessor") PopularBookItemProcessor processor,
      @Qualifier("bookWriter") JpaItemWriter<PopularBook> writer)
  {
    return new StepBuilder("aggregatePopularBookStep",jobRepository)
        .<Book, PopularBook>chunk(100, transactionManager) // 100 청크 사이즈만큼 처리
        .reader(itemReader) // 요소를 읽어들이는 리더
        .processor(processor) // 요소를 가공하는 프로세서
        .writer(writer) // 요소를 쓰는 라이터
        .build();
  }

  @Bean
  // 랭크를 부여하는 스텝임.
  public Step rankPopularBooksStep(RankPopularBookTasklet rankPopularBookTasklet){
    return new StepBuilder("rankPopularBooksStep",jobRepository)
        .tasklet(rankPopularBookTasklet, transactionManager)
        .build();
  }

}
