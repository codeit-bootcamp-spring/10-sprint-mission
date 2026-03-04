package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ChannelDto {
    private ChannelDto() {}

    public record channelCreatePrivateRequest(List<UUID> participantIds) {}
    public record channelCreatePublicRequest(@JsonProperty("name") String title, String description) {}
    public record channelUpdatePublicRequest(@JsonProperty("newName") String title, @JsonProperty("newDescription") String description) {}
    public record channelResponse(@JsonProperty("id") UUID uuid, Instant createdAt, Instant updatedAt,
                                  @JsonProperty("type") ChannelType channelType, @JsonProperty("name") String title, String description,
                                  List<UUID> participantIds, Instant lastMessageAt) {}
}
