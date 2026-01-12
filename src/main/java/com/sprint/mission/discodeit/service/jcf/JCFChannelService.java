package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.put(channel.getId(), channel);
        return channel;
    }

    public Channel findById(UUID channelId) {
        return data.get(channelId);
    }

    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    public void update(UUID channelId, String name) {
        Channel channel = data.get(channelId);
        channel.updateName(name);
    }

    public void delete(UUID channelId) {
        data.remove(channelId);
    }
}
