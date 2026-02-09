package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    ChannelResponseDto createPublic(PublicChannelCreateDto publicChannelCreateDto);
    ChannelResponseDto createPrivate(PrivateChannelCreateDto privateChannelCreateDto);

    // Read
    ChannelResponseDto findById(UUID id);

    // ReadAll
    List<ChannelResponseDto> findAllByUserId(UUID userId);

    // Update
    ChannelResponseDto update(UUID id, ChannelUpdateDto channelUpdateDto);

    // 채널 참여
    ChannelResponseDto joinChannel(UUID userId, UUID channelId);


    // Delete
    void delete(UUID id);
}
