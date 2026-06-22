package com.sprint.mission.discodeit.readstatus.application.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.sprint.mission.discodeit.readstatus.domain.repository.ReadStatusReferenceRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadStatusReferenceQuerySupplierTest {

  @Mock
  private ReadStatusReferenceRepository repository;

  @InjectMocks
  private ReadStatusReferenceQuerySupplier supplier;

  @Test
  @DisplayName("findUserIdsByChannelIdAndNotificationEnabledIsTrue - 유저 ID 목록을 정상 반환한다.")
  void findUserIds_success() {
    UUID channelId = UUID.randomUUID();
    List<UUID> expectedIds = List.of(UUID.randomUUID(), UUID.randomUUID());

    given(repository.findUserIdsByChannel_IdAndNotificationEnabledIsTrue(channelId)).willReturn(expectedIds);

    List<UUID> actualIds = supplier.findUserIdsByChannelIdAndNotificationEnabledIsTrue(channelId);

    assertThat(actualIds).isEqualTo(expectedIds);
  }
}
