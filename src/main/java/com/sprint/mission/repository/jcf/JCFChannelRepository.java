package com.sprint.mission.repository.jcf;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.repository.ChannelRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;

    public JCFChannelRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findByUserId(UUID userId) {
        return data.values().stream()
                .filter(channel ->
                        channel.getUsers().stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
