package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.CreatePrivateChannelRequest;
import com.sprint.mission.discodeit.dto.channel.CreatePublicChannelRequest;
import com.sprint.mission.discodeit.dto.channel.UpdateChannelRequest;

import java.util.List;
import java.util.UUID;

public interface ChannelService {

    UUID createPublicChannel(CreatePublicChannelRequest request);

    UUID createPrivateChannel(CreatePrivateChannelRequest request);

    ChannelResponse findChannelByChannelId(UUID id);

    List<ChannelResponse> findAllChannelsByUserId(UUID requesterId);

    ChannelResponse updateChannelInfo(UpdateChannelRequest request);

    void deleteChannel(UUID channelId);
}
