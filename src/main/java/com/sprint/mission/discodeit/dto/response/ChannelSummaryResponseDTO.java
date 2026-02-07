package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.UUID;

public record ChannelSummaryResponseDTO(
        UUID channelId,
        ChannelType channelType,
        String channelName,
        String description
) {}
