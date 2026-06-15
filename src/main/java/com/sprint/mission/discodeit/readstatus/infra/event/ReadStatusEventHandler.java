package com.sprint.mission.discodeit.readstatus.infra.event;

import com.sprint.mission.discodeit.channel.domain.event.ReadStatusCreatedEvent;
import com.sprint.mission.discodeit.readstatus.application.service.ReadStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReadStatusEventHandler {

  private final ReadStatusService service;

  @EventListener
  public void handleReadStatusCreatedEvent(ReadStatusCreatedEvent event) {
    service.create(event.channelId(), event.participantIds(), event.notificationEnabled());
  }
}
