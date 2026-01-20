package com.sprint.mission.service.basic;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.EntityType;
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.service.ChannelService;

public class BasicChannelService extends BasicBaseService<Channel, ChannelRepository> implements ChannelService {
    public BasicChannelService(ChannelRepository repository) {
        super(repository, EntityType.CHANNEL);
    }

    @Override
    public Channel create(String channelName) {
        Channel channel = new Channel(channelName);
        repository.write(channel);
        return channel;
    }
}
