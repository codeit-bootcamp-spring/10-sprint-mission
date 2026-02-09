package com.sprint.mission.discodeit.channel.service;

import com.sprint.mission.discodeit.channel.dto.*;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    PublicChannelInfo createPublicChannel(PublicChannelCreateInfo channelInfo);
    PrivateChannelInfo createPrivateChannel(PrivateChannelCreateInfo channelInfo);
    ChannelInfo findChannel(UUID channelId);
    List<ChannelInfo> findAll();
    List<ChannelInfo> findAllByUserId(UUID userId);
    ChannelInfo updateChannel(UpdateChannelInfo channelInfo);
    void deleteChannel(UUID channelId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
