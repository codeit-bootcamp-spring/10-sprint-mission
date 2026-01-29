package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class ChannelResponseDTO {
    private UUID channelId;
    private String channelName;
    private List<UUID> members;
    private ChannelType channelType;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
