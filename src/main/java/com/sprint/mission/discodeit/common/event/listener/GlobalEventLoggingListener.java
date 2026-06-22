package com.sprint.mission.discodeit.common.event.listener;

import com.sprint.mission.discodeit.common.event.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalEventLoggingListener {

  @EventListener
  public void handleDomainEvent(DomainEvent event) {
    log.info("[Event Published] type: {}, eventId: {}, correlationId: {}",
        event.getEventType(), event.getEventId(), event.getCorrelationId());
  }
}
