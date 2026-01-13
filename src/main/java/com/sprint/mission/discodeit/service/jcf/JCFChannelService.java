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
        Channel channel = data.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널입니다. id= " + channelId);
        }
        return channel;
    }

    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    public Channel update(UUID channelId, String name) {
        Channel channel = findById(channelId);
        channel.updateName(name);
        return channel;
    }

    public void delete(UUID channelId) {
        findById(channelId);
        data.remove(channelId);
    }
}
