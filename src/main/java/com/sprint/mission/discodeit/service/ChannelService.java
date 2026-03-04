package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDTO createPublicChannel(CreatePublicChannelRequestDTO dto);

    ChannelResponseDTO createPrivateChannel(CreatePrivateChannelRequestDTO dto);

    List<ChannelWithLastMessageDTO> findAllByUserId(UUID userId);

    ChannelWithLastMessageDTO findByChannelId(UUID channelId);

    ChannelWithLastMessageDTO updateChannel(UUID channelId, UpdateChannelRequestDTO dto);

    void deleteChannel(UUID channelId);
}
