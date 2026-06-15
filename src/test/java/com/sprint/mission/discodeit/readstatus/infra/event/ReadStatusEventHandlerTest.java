package com.sprint.mission.discodeit.readstatus.infra.event;

import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.channel.domain.event.ReadStatusCreatedEvent;
import com.sprint.mission.discodeit.readstatus.application.service.ReadStatusService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReadStatusEventHandlerTest {

  @Mock
  private ReadStatusService service;

  @InjectMocks
  private ReadStatusEventHandler handler;

  @Test
  @DisplayName("handleReadStatusCreatedEvent - 이벤트를 수신하여 읽음 상태 생성을 위임한다.")
  void handleReadStatusCreatedEvent_success() {
    UUID channelId = UUID.randomUUID();
    List<UUID> participantIds = List.of(UUID.randomUUID(), UUID.randomUUID());
    ReadStatusCreatedEvent event = new ReadStatusCreatedEvent(channelId, participantIds, true);

    handler.handleReadStatusCreatedEvent(event);

    verify(service).create(channelId, participantIds, true);
  }
}
