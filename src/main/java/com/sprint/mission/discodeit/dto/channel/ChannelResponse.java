package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelResponse(
    @JsonProperty("id")
    UUID channelId,

    @JsonProperty("name")
    String channelName,

    String description,

    @JsonProperty("private")
    boolean isPrivate,

    @JsonProperty("lastMessageAt")
    Instant lastMessageTime,

    List<UUID> participantIds
) {

}