package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record ChannelUpdateRequest(
    @JsonIgnore
    UUID channelId,

    @JsonProperty("newName")
    String name,

    @JsonProperty("newDescription")
    String description
) {

}