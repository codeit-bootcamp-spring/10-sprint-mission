package com.sprint.mission.discodeit.notification.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.channel.domain.entity.Channel;
import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationChannelAdapterTest {

  @Mock
  private EntityReferenceSupplier<Channel> service;

  @InjectMocks
  private NotificationChannelAdapter adapter;

  @Test
  @DisplayName("getChannelName - 채널 이름이 존재하면 반환한다.")
  void getChannelName_hasName() {
    UUID channelId = UUID.randomUUID();
    Channel channel = mock(Channel.class);
    given(channel.getName()).willReturn("general");
    given(service.getOrThrow(channelId)).willReturn(channel);

    String result = adapter.getChannelName(channelId);

    assertThat(result).isEqualTo("general");
  }

  @Test
  @DisplayName("getChannelName - 채널 이름이 없으면 anonymous를 반환한다.")
  void getChannelName_noName() {
    UUID channelId = UUID.randomUUID();
    Channel channel = mock(Channel.class);
    given(channel.getName()).willReturn(null);
    given(service.getOrThrow(channelId)).willReturn(channel);

    String result = adapter.getChannelName(channelId);

    assertThat(result).isEqualTo("anonymous");
  }
}
