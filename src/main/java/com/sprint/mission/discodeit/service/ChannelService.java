package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelCreatePrivateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelCreatePublicDto;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponsePrivateDto;
import com.sprint.mission.discodeit.dto.channel.response.ChannelResponsePublicDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto createPublic(ChannelCreatePublicDto channelCreatePublicDto);
    ChannelResponseDto createPrivate(ChannelCreatePrivateDto channelCreatePrivateDto);
    ChannelResponseDto joinUsers(UUID channelId, UUID ...userId);
    ChannelResponseDto findChannel(UUID channelId);
    List<ChannelResponseDto> findAllChannelsByUserId(UUID userId);
    ChannelResponseDto update(ChannelUpdateDto channelUpdateDto);
    void delete(UUID channelId);
}
