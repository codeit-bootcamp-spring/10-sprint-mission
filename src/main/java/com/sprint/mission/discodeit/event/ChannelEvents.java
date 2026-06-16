package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;

public final class ChannelEvents {
    private ChannelEvents() {}

    public record Created(UUID id, ChannelType type, List<UUID> participantIds) {}
    public record Updated(UUID id, ChannelType type, List<UUID> participantIds) {}
    public record Deleted(UUID id, ChannelType type, List<UUID> participantIds) {}
    public record AccessChanged(UUID userId) {}
}
