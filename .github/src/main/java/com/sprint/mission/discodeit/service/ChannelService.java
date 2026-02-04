package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto.Response create(ChannelDto.CreateRequest request);
    ChannelDto.Response find(UUID channelId);
    List<ChannelDto.Response> findAllByUserId(UUID userId);
    ChannelDto.Response update(ChannelDto.UpdateRequest request);
    void delete(UUID channelId);
}
