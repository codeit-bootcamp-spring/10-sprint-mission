package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFChannelRepository implements ChannelRepository {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public Channel save(Channel channel) {
        int index = -1;
        for (int i = 0; i <  channels.size(); i++) {
            if (channels.get(i).getId().equals(channel.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            channels.set(index, channel);
        } else {
            channels.add(channel);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return channels.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    @Override
    public void deleteById(UUID id) {
        channels.removeIf(c -> c.getId().equals(id));
    }
}
