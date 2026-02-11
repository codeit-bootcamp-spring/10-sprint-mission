package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.CreatePrivateChannelRequestDto;
import com.sprint.mission.discodeit.dto.CreatePublicChannelRequestDto;
import com.sprint.mission.discodeit.dto.UpdateChannelRequestDto;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublic(CreatePublicChannelRequestDto request);
    ChannelDto createPrivate(CreatePrivateChannelRequestDto request);

    ChannelDto find(UUID channelId);
    // 전체 조회
    List<ChannelDto> findAll();
    // 유저 id 기준 조회
    List<ChannelDto> findAllByUserId(UUID userId);
    ChannelDto update(UUID channelId, UpdateChannelRequestDto request);
    void delete(UUID channelId);
}
