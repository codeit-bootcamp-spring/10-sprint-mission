package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PublicChannelCreateRequest(
    @JsonProperty("name")
    String name,

    @JsonProperty("description")
    String description
) {

}