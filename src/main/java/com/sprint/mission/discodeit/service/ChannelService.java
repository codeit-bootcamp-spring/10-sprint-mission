package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.channel.ChannelInfo;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateInfo;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelInfo;
import com.sprint.mission.discodeit.dto.channel.PublicChannelInfo;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

import java.util.List;
import java.util.UUID;

public interface ChannelService {
    PublicChannelInfo createPublicChannel(PublicChannelInfo channelInfo);
    PrivateChannelInfo createPrivateChannel(PrivateChannelCreateInfo channelInfo);
    ChannelInfo findChannel(UUID channelId);
    List<ChannelInfo> findAll();
    List<ChannelInfo> findAllByUserId(UUID userId);
    ChannelInfo updateChannel(PublicChannelInfo channelInfo);
    void deleteChannel(UUID channelId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
}
