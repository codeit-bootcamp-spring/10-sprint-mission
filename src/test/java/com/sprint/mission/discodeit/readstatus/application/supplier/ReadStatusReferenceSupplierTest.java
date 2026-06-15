package com.sprint.mission.discodeit.readstatus.application.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusErrorCode;
import com.sprint.mission.discodeit.readstatus.domain.exception.ReadStatusException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadStatusReferenceSupplierTest {

  @Mock
  private DomainRepository<ReadStatus> repository;

  @InjectMocks
  private ReadStatusReferenceSupplier supplier;

  @Test
  @DisplayName("getProxy - 단일 ID로 프록시를 정상 반환한다.")
  void getProxy_single_success() {
    UUID id = UUID.randomUUID();
    ReadStatus expected = mock(ReadStatus.class);

    given(repository.getReferenceById(id)).willReturn(expected);

    ReadStatus actual = supplier.getProxy(id);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("getProxy - 다수 ID로 프록시 목록을 정상 반환한다.")
  void getProxy_multiple_success() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<UUID> ids = List.of(id1, id2);
    ReadStatus expected1 = mock(ReadStatus.class);
    ReadStatus expected2 = mock(ReadStatus.class);

    given(repository.getReferenceById(id1)).willReturn(expected1);
    given(repository.getReferenceById(id2)).willReturn(expected2);

    List<ReadStatus> actual = supplier.getProxy(ids);

    assertThat(actual).containsExactly(expected1, expected2);
  }
}
