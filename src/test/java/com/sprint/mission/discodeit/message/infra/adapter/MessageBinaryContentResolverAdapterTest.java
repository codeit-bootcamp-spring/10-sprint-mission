package com.sprint.mission.discodeit.message.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.binarycontent.domain.entity.BinaryContent;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.Mockito.mock;

@DisplayName("MessageBinaryContentResolverAdapter Test")
@ExtendWith(MockitoExtension.class)
class MessageBinaryContentResolverAdapterTest {

  @Mock
  private EntityReferenceSupplier<BinaryContent> service;

  @InjectMocks
  private MessageBinaryContentResolverAdapter adapter;

  @Test
  @DisplayName("getProxyOrThrow - ID 리스트로 BinaryContent 엔티티 프록시 리스트를 정상 반환한다.")
  void getProxyOrThrow_success() {
    UUID id = UUID.randomUUID();
    BinaryContent expected = mock(BinaryContent.class);
    ReflectionTestUtils.setField(expected, "id", id);
    List<UUID> ids = List.of(id);
    given(service.getProxy(ids)).willReturn(List.of(expected));

    List<BinaryContent> actual = adapter.getProxyOrThrow(ids);

    assertThat(actual).containsExactly(expected);
  }
}
