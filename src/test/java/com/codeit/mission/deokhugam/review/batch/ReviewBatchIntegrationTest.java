package com.codeit.mission.deokhugam.review.batch;

import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBatchTest
@SpringBootTest
@Transactional
class ReviewBatchIntegrationTest {

  @Autowired
  @Qualifier("reviewHardDeleteReader")
  private JpaCursorItemReader<UUID> reviewHardDeleteReader;

  @Test
  @DisplayName("Batch Reader: StepScope 환경에서 삭제 대상 쿼리 및 파라미터 매핑 검증")
  void verifyReviewHardDeleteReaderLogic() throws Exception {
    // given
    String thresholdStr = Instant.now().toString();

    // StepExecution 생성
    StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution();

    stepExecution.getJobExecution().getExecutionContext().putString("threshold", thresholdStr);

    // when & then
    StepScopeTestUtils.doInStepScope(stepExecution, () -> {

      // reader 초기화
      reviewHardDeleteReader.open(stepExecution.getExecutionContext());

      try {
        // 실행 결과 확인
        UUID result = reviewHardDeleteReader.read();
        assertNull(result);
      } finally {
        // reader 자원 해제
        reviewHardDeleteReader.close();
      }

      return null;
    });
  }
}
