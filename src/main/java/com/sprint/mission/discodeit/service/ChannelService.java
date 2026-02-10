package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface ChannelService extends DomainService<ChannelResponse, ChannelCreateRequest, PublicChannelUpdateRequest> {
    List<ChannelResponse> findAllByUserId(UUID userId) throws IOException;
}
