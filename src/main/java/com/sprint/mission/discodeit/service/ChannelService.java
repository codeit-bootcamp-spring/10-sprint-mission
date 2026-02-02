package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    Channel createPrivateChannel(ChannelRequestDto channelCreateDto);
    Channel createPublicChannel(ChannelRequestDto channelCreateDto);
    ChannelResponseDto find(UUID channelId);
//    List<ChannelResponseDto> findAll();
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    Channel update(UUID channelId,ChannelRequestDto channelUpdateDto);
    void delete(UUID channelId);


}
