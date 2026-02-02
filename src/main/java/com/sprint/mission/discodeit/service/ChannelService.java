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

    ChannelWithLastMessageDTO updateChannel(UpdateChannelRequestDTO dto);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

    void deleteChannel(UUID channelId);
}
