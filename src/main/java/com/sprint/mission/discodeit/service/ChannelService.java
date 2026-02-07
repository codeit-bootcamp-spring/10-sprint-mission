package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.ChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PrivateCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelDetailResponseDTO;
import com.sprint.mission.discodeit.dto.response.ChannelSummaryResponseDTO;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelSummaryResponseDTO create(PublicCreateRequestDTO publicCreateRequestDTO);
    ChannelSummaryResponseDTO create(PrivateCreateRequestDTO privateCreateRequestDTO);
//    Channel find(UUID channelId);
    ChannelDetailResponseDTO find (UUID channelId);
    List<ChannelDetailResponseDTO> findAllByUserId(UUID userId);
    ChannelSummaryResponseDTO update(UUID channelId, ChannelUpdateRequestDTO channelUpdateRequestDTO);
    void delete(UUID channelId);
}
