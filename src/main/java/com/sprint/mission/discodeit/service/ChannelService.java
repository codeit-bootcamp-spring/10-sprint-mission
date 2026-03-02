package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelDto;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelUpdateRequest;
import com.sprint.mission.discodeit.entity.Channel;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    Channel createPublicChannel(PublicChannelCreateRequest request);

    Channel createPrivateChannel(PrivateChannelCreateRequest request);

    Channel findChannelByChannelId(UUID channelId);

    List<ChannelDto> findAllChannelsByUserId(UUID requesterId);

    Channel updateChannelInfo(UUID channelId, PublicChannelUpdateRequest request);

    void deleteChannel(UUID channelId);
}
