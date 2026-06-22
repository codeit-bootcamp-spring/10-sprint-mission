package com.sprint.mission.discodeit.binarycontent.infra.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.entity.constant.BinaryContentStatus;
import com.sprint.mission.discodeit.binarycontent.domain.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.support.base.BaseRepositoryTest;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BinaryContentRepositoryTest extends BaseRepositoryTest {

  @Autowired
  private BinaryContentRepository repository;

  @Test
  @DisplayName("updateStatus - 주어진 ID 목록의 상태를 정상적으로 변경한다.")
  void updateStatus_success() {
    BinaryContent content = BinaryContent.builder()
        .fileName("test.txt")
        .contentType("text/plain")
        .size(100L)
        .build();
    repository.save(content);
    flushAndClear();
    clear();

    int count = repository.updateStatus(List.of(content.getId()), BinaryContentStatus.SUCCESS);
    assertThat(count).isEqualTo(1);
    ensureQueryCount(1);

    clear();
    BinaryContent updated = repository.findById(content.getId()).orElseThrow();
    assertThat(updated.getStatus()).isEqualTo(BinaryContentStatus.SUCCESS);
  }

  @Test
  @DisplayName("existsSuccessById - SUCCESS 상태인 데이터가 존재하는지 정상 확인한다.")
  void existsSuccessById_success() {
    BinaryContent content = BinaryContent.builder()
        .fileName("test.txt")
        .contentType("text/plain")
        .size(100L)
        .build();
    repository.save(content);
    flushAndClear();
    clear();

    repository.updateStatus(List.of(content.getId()), BinaryContentStatus.SUCCESS);
    clear();

    boolean exists = repository.existsSuccessById(content.getId());
    assertThat(exists).isTrue();
    ensureQueryCount(1);
  }
}
