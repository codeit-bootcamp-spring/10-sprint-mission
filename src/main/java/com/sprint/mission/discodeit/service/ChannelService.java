package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelCreation;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelUpdate;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelService extends DomainService<ChannelResponse, ChannelCreation, PublicChannelUpdate> {
    List<ChannelResponse> findAllByUserId(UUID userId) throws IOException;
}
