package com.sprint.mission.discodeit.binarycontent.application.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentErrorCode;
import com.sprint.mission.discodeit.binarycontent.domain.exception.BinaryContentException;
import com.sprint.mission.discodeit.common.exception.DiscodeitException;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BinaryContentReferenceSupplierTest {

  @Mock
  private DomainRepository<BinaryContent> repository;

  @InjectMocks
  private BinaryContentReferenceSupplier supplier;

  @Test
  @DisplayName("getProxy - 단일 ID로 프록시를 정상 반환한다.")
  void getProxy_single_success() {
    UUID id = UUID.randomUUID();
    BinaryContent expected = mock(BinaryContent.class);

    given(repository.getReferenceById(id)).willReturn(expected);

    BinaryContent actual = supplier.getProxy(id);

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  @DisplayName("getProxy - 다수 ID로 프록시 목록을 정상 반환한다.")
  void getProxy_multiple_success() {
    UUID id1 = UUID.randomUUID();
    UUID id2 = UUID.randomUUID();
    List<UUID> ids = List.of(id1, id2);
    BinaryContent expected1 = mock(BinaryContent.class);
    BinaryContent expected2 = mock(BinaryContent.class);

    given(repository.getReferenceById(id1)).willReturn(expected1);
    given(repository.getReferenceById(id2)).willReturn(expected2);

    List<BinaryContent> actual = supplier.getProxy(ids);

    assertThat(actual).containsExactly(expected1, expected2);
  }

  @Test
  @DisplayName("notFoundException - 단일 ID 조회 실패 시 예외를 반환한다.")
  void notFoundException_single() {
    UUID id = UUID.randomUUID();
    DiscodeitException exception = supplier.notFoundException(id);
    assertThat(exception).isInstanceOf(BinaryContentException.class)
        .hasFieldOrPropertyWithValue("errorCode", BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND);
  }

  @Test
  @DisplayName("notFoundException - 다수 ID 조회 실패 시 예외를 반환한다.")
  void notFoundException_multiple() {
    List<UUID> ids = List.of(UUID.randomUUID());
    DiscodeitException exception = supplier.notFoundException(ids);
    assertThat(exception).isInstanceOf(BinaryContentException.class)
        .hasFieldOrPropertyWithValue("errorCode", BinaryContentErrorCode.BINARYCONTENTID_NOT_FOUND);
  }
}
