package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.ChannelType;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ChannelDetailResponseDTO(
   UUID channelId,
   ChannelType channelType,
   String channelName,
   String description,
   List<UUID> participantIds,
   Instant recentMessageTime
) {}
