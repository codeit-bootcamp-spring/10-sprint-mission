package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;

public interface ChannelService {
    Channel createChannel(ChannelType type, String channelName, String channelDescription);
}
