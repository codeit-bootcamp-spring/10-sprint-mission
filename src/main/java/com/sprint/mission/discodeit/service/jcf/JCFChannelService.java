package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> channels = new ArrayList<>();

    @Override
    public void create(Channel entity) {
        channels.add(entity);
    }

    @Override
    public Channel findById(UUID uuid) {
        return channels.stream()
                .filter(c -> c.getId().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. ID: "  + uuid));
    }


    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    @Override
    public void update(UUID uuid, Channel entity) {
        findById(uuid);

        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(uuid)) {
                channels.set(i, entity);
                return;
            }
        }
    }

    @Override
    public void delete(UUID uuid) {
        Channel channel = findById(uuid);
        channels.remove(channel);
    }
}
