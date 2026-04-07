package com.sprint.mission.discodeit.dto.channel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record ChannelUpdateRequest(

    @JsonIgnore
    UUID channelId,

    @NotBlank(message = "채널이름은 필수입니다.")
    @JsonProperty("newName")
    String name,

    @Size(max = 255, message = "설명은 255자 이하입니다.")
    @JsonProperty("newDescription")
    String description
) {

}
