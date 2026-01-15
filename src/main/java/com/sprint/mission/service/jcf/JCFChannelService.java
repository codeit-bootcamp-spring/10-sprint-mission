package com.sprint.mission.service.jcf;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.service.ChannelService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel create(String channelId) {
        return create(channelId, data, Channel::new);
    }

    @Override
    public Channel get(UUID uuid) {
        return get(uuid, data);
    }

    @Override
    public List<Channel> getAll(List<UUID> uuids) {
        return getAll(uuids, data);
    }

    @Override
    public void update(UUID uuid, String newChannelId) {
        update(uuid, newChannelId, data);
    }

    @Override
    public void delete(UUID uuid) {
        delete(uuid, data);
    }
}
