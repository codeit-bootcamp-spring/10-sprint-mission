package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> data = new ArrayList<>();
    @Override
    public Channel save(Channel channel) {
        data.removeIf(c -> c.getId().equals(channel.getId()));
        data.add(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(c -> c.getId().equals(id));
    }
}
