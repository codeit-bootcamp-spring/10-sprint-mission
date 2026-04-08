package com.sprint.mission.discodeit.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sprint.mission.discodeit.entity.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDto(
        UUID id,
        String name,
        String description,
        ChannelType type,

        @JsonProperty("lastMessageAt")
        @Schema(name = "lastMessageAt")
        Instant lastMessageAt,

        @JsonProperty("participants")
        @Schema(name = "participants")
        List<UserDto> participants
) {
}
