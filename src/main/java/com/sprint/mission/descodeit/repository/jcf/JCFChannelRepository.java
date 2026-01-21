package com.sprint.mission.descodeit.repository.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.repository.ChannelRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        data = new HashMap<>();
    }

    @Override
    public Channel findById(UUID channelId) {
        return data.get(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(UUID channelId, Channel channel) {
        data.put(channelId, channel);

    }

    @Override
    public void delete(UUID channelId) {
        data.remove(channelId);
    }
}
