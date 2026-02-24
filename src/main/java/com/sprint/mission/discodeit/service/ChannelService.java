package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.ChannelSummaryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelSummaryResponseDTO create(PublicChannelCreateRequestDTO publicChannelCreateRequestDTO);
    ChannelSummaryResponseDTO create(PrivateChannelCreateRequestDTO privateChannelCreateRequestDTO);
    ChannelDetailResponseDTO find (UUID channelId);
    List<ChannelDetailResponseDTO> findAllByUserId(UUID userId);
    ChannelSummaryResponseDTO update(UUID channelId, ChannelUpdateRequestDTO channelUpdateRequestDTO);
    void delete(UUID channelId);
}
