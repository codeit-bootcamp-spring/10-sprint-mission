package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto createPublic(CreatePublicChannelRequestDto createPublicChannelRequestDto);

    ChannelResponseDto createPrivate(CreatePrivateChannelRequestDto createPrivateChannelRequestDto);

    ChannelResponseDto find(UUID id);

    List<ChannelResponseDto> findAllByUserId(UUID id);

    ChannelResponseDto updateChannel(UpdateChannelRequestDto updateChannelRequestDto);

    void deleteChannel(UUID id);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

}
