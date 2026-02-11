package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    ChannelInfoDto createPublic(PublicChannelCreateDto publicChannelCreateDto);
    ChannelInfoDto createPrivate(PrivateChannelCreateDto privateChannelCreateDto);

    // Read
    ChannelInfoDto findById(UUID id);

    // ReadAll
    List<ChannelInfoDto> findAllByUserId(UUID userId);

    // Update
    ChannelInfoDto update(ChannelUpdateDto channelUpdateDto);

    // 채널 참여
    void joinChannel(UUID userId, UUID channelId);


    // Delete
    void delete(UUID id);
}
