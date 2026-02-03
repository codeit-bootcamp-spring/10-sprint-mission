package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelCreation;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelInfoUpdate;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;

import java.util.List;
import java.util.UUID;

public interface ChannelService extends DomainService<ChannelResponse, ChannelCreation, ChannelInfoUpdate> {
    List<ChannelResponse> findAllByUserId(UUID userId);
}
