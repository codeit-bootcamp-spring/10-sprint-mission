package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public void save(Channel channel) {
        channels.add(channel);
    }

    @Override
    public Channel findById(UUID id) {
        return channels.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    @Override
    public void update(Channel channel) {
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(channel.getId())) {
                channels.set(i, channel);
                return;
            }
        }
    }

    @Override
    public void delete(UUID id) {
        channels.removeIf(c -> c.getId().equals(id));
    }
}
