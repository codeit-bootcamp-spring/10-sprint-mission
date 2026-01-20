package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> channelDB = new HashMap<>();

    @Override
    public Channel save(Channel channel) {
        channelDB.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(channelDB.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelDB.values());
    }

    @Override
    public void delete(UUID id) {
        channelDB.remove(id);
    }

    @Override
    public boolean existsByName(String name) {
        return channelDB.values().stream()
                .anyMatch(ch -> ch.getChannelName().equals(name));
    }

    @Override
    public List<Channel> findAllByOwnerId(UUID ownerId) {
        return channelDB.values().stream()
                .filter(ch -> ch.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }
}