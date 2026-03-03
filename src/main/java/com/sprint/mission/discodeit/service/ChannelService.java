package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelServiceDTO;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.ChannelResponse;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.ChannelServiceDTO.PublicChannelUpdateCommand;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponse find(UUID id);

    List<ChannelResponse> findAllByUserId(UUID userId);

    ChannelResponse createPublic(PublicChannelCreateRequest request);

    ChannelResponse createPrivate(PrivateChannelCreateRequest request);

    ChannelResponse update(PublicChannelUpdateCommand command);

    void delete(UUID id);
}
