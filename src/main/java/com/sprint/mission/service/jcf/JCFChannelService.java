package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.service.ChannelService;

import java.util.Map;
import java.util.UUID;

public class JCFChannelService extends JCFBaseService<Channel> implements ChannelService {

    public JCFChannelService() {
        super();
    }

    public JCFChannelService(Map<UUID, Channel> data) {
        super(data);
    }

    @Override
    public Channel create(String channelName) {
        Channel channel = new Channel(channelName);
        getData().put(channel.getId(), channel);
        return channel;
    }
}
