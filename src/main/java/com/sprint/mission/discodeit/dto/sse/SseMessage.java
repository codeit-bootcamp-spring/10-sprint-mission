package com.sprint.mission.discodeit.dto.sse;

import lombok.Getter;

import java.util.UUID;

@Getter
public class SseMessage {
    private UUID eventId;
    private String eventName;
    private Object data;
}
