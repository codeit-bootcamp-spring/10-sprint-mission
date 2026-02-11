package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto create(PublicChannelCreateDto channelCreateDto);
    ChannelResponseDto create(PrivateChannelCreateDto channelCreateDto);
    ChannelResponseDto find(UUID channelId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    ChannelResponseDto update(UUID id,ChannelUpdateDto  channelUpdateDto);
    void delete(UUID channelId);
}
