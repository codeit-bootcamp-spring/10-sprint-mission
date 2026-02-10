package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponsePrivateDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponsePublicDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto create(ChannelCreateDto dto);
    ChannelResponseDto joinUsers(UUID channelId, UUID ...userId);
    ChannelResponseDto findChannel(UUID channelId);
    List<ChannelResponseDto> findAllChannelsByUserId(UUID userId);
    List<ChannelResponseDto> findAllChannels();
    ChannelResponseDto update(UUID channelId, ChannelUpdateDto dto);
    void delete(UUID channelId);
}
