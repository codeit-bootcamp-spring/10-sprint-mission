package com.sprint.mission.discodeit.dto.request.channel;

import com.sprint.mission.discodeit.entity.ChannelType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChannelUpdateRequestDTO {
    @Setter
    @NotNull
    private UUID id;

    @NotNull
    private ChannelType channelType;

    @NotBlank
    private String channelName;

    @NotBlank
    private String description;
}
