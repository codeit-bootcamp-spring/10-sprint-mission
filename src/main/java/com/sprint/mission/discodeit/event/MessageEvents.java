package com.sprint.mission.discodeit.event;

import java.util.UUID;

public final class MessageEvents {
    private MessageEvents() {}

    public record Created(UUID messageId) {}
}
