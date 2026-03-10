package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PublicChannelUpdateRequest(
    @JsonProperty("newName")
    String newName,

    @JsonProperty("newDescription")
    String newDescription
) {

}