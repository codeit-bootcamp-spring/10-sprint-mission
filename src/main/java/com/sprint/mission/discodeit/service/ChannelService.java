package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    ChannelDto createPublic(PublicChannelCreateRequest request);
    ChannelDto createPrivate(PrivateChannelCreateRequest request);
    ChannelDto findChannel(UUID channelId);
    List<ChannelDto> findAllPublicChannelsByUserId();
    List<ChannelDto> findAllPrivateChannelsByUserId(UUID userId);
    List<ChannelDto> findAllChannels();
    ChannelDto update(UUID channelId, PublicChannelUpdateRequest dto);
    void delete(UUID channelId);
}
