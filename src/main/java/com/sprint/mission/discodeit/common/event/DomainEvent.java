package com.sprint.mission.discodeit.common.event;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

@Getter
public abstract class DomainEvent {

  private final UUID eventId;
  private final Instant timestamp;
  private final String correlationId;
  private final String eventType;

  protected DomainEvent() {
    eventId = UUID.randomUUID();
    timestamp = Instant.now();
    String traceId = MDC.get("traceId");
    this.correlationId = (StringUtils.hasText(traceId)) ? traceId : UUID.randomUUID().toString();
    this.eventType = this.getClass().getSimpleName();
  }
}
