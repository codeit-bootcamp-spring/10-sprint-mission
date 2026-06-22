package com.sprint.mission.discodeit.notification.infra.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.common.reference.supplier.EntityReferenceSupplier;
import com.sprint.mission.discodeit.readstatus.application.supplier.ReadStatusReferenceQuerySupplier;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationReadStatusAdapterTest {

  @Mock
  private EntityReferenceSupplier<ReadStatus> service;

  @Mock
  private ReadStatusReferenceQuerySupplier querySupplier;

  @InjectMocks
  private NotificationReadStatusAdapter adapter;

  @Test
  @DisplayName("findUserIdsByChannelIdAndNotificationEnabledIsTrue - 채널의 알림을 활성화한 유저 목록을 반환한다.")
  void findUserIdsByChannelIdAndNotificationEnabledIsTrue() {
    UUID channelId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    given(querySupplier.findUserIdsByChannelIdAndNotificationEnabledIsTrue(channelId)).willReturn(List.of(userId));

    List<UUID> result = adapter.findUserIdsByChannelIdAndNotificationEnabledIsTrue(channelId);

    assertThat(result).hasSize(1);
    assertThat(result.get(0)).isEqualTo(userId);
  }
}
