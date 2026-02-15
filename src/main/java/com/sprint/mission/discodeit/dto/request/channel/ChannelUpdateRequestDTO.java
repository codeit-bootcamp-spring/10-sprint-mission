package com.sprint.mission.discodeit.dto.request.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ChannelUpdateRequestDTO (
        @NotNull
        ChannelType channelType,

        @NotBlank
        String channelName,

        @NotBlank
        String description
) {

}
