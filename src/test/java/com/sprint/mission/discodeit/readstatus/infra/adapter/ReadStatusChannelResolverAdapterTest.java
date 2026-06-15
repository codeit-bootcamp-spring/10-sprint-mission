package com.sprint.mission.discodeit.readstatus.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("ReadStatusChannelAdapter Test")
@ExtendWith(MockitoExtension.class)
class ReadStatusChannelResolverAdapterTest {

  @Mock
  private EntityReferenceSupplier<Channel> service;

  @InjectMocks
  private ReadStatusChannelAdapter adapter;

  @Test
  @DisplayName("getProxyOrThrow - ID로 Channel 엔티티 프록시를 정상 반환한다.")
  void getProxyOrThrow_success() {
    UUID id = UUID.randomUUID();
    Channel expected = mock(Channel.class);
    ReflectionTestUtils.setField(expected, "id", id);
    
    given(service.getProxy(id)).willReturn(expected);

    Channel actual = adapter.getProxyOrThrow(id);

    assertThat(actual).isEqualTo(expected);
}
}

