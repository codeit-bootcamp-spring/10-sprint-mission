package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelFindDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequestDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPublicChannel(PublicChannelCreateRequestDto requestDto);
    Channel createPrivateChannel(PrivateChannelCreateRequestDto requestDto);
    ChannelFindDto find(UUID channelId);
    List<ChannelFindDto> findAllByUserId(UUID userId);
    Channel update(UUID channelId, String newName, String newDescription);
    void delete(UUID channelId);
}
