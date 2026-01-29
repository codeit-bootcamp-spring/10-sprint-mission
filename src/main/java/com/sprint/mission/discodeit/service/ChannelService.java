package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelCreateDto;
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.ChannelUpdateDto;
import com.sprint.mission.discodeit.dto.PrivateChannelCreateDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelResponseDto createPublicChannel(ChannelCreateDto dto);
    ChannelResponseDto createPrivateChannel(PrivateChannelCreateDto dto);

    ChannelResponseDto find(UUID channelId);
    List<ChannelResponseDto> findAllByUserId(UUID userId);
    ChannelResponseDto update(ChannelUpdateDto dto);
    void delete(UUID channelId);
}
