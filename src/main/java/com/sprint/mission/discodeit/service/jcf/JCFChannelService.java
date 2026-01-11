package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;

public class JCFChannelService implements ChannelService {
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
        return new Channel(type, channelName, channelDescription);
    }
}
