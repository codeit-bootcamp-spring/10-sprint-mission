package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelInfoDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.dto.PublicChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    // Create
    Channel createPublic(PublicChannelCreateDto publicChannelCreateDto);
    Channel createPrivate(PrivateChannelCreateDto privateChannelCreateDto);

    // Read
    ChannelInfoDto findById(UUID id);

    // ReadAll
    List<ChannelInfoDto> findAllByUserId(UUID userId);

    // Update
    Channel update(ChannelUpdateDto channelUpdateDto);

    // 채널 참여
    void joinChannel(UUID userId, UUID channelId);

    List<UUID> getChannelMessageIds(UUID channelId);

    List<UUID> getChannelUserIds(UUID channelId);

    // Delete
    void delete(UUID id);


}
