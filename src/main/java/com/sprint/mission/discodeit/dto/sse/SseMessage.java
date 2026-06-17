package com.sprint.mission.discodeit.dto.sse;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class SseMessage {
    private UUID receiverId;
    private UUID eventId;
    private String eventName;
    private Object data;
}
