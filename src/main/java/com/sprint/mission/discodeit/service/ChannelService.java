package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.request.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.response.ChannelDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto create(PublicChannelCreateRequest publicChannelCreateRequest);
    ChannelDto create(PrivateChannelCreateRequest privateChannelCreateRequest);
    ChannelDto find (UUID channelId);
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(UUID channelId, PublicChannelUpdateRequest publicChannelUpdateRequest);
    void delete(UUID channelId);
}
