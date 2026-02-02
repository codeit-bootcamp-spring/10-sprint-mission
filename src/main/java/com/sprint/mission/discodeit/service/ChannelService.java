package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.PrivateChannelRequestCreateDto;
import com.sprint.mission.discodeit.dto.channel.PublicChannelRequestCreateDto;
import com.sprint.mission.discodeit.dto.channel.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.channel.ChannelRequestUpdateDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto createPublic(PublicChannelRequestCreateDto publicChannelRequestCreateDto);

    ChannelResponseDto createPrivate(PrivateChannelRequestCreateDto privateChannelRequestCreateDto);

    ChannelResponseDto find(UUID id);

    List<ChannelResponseDto> findAllByUserId(UUID id);

    ChannelResponseDto updateChannel(ChannelRequestUpdateDto channelRequestUpdateDto);

    void deleteChannel(UUID id);

    void joinChannel(UUID channelId, UUID userId);

    void leaveChannel(UUID channelId, UUID userId);

}
