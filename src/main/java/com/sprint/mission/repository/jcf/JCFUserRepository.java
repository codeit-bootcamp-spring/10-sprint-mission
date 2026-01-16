package com.sprint.mission.repository.jcf;

import com.sprint.mission.entity.User;
import com.sprint.mission.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserRepository implements UserRepository {
    Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(user ->
                        user.getChannels().stream()
                                .anyMatch(channel -> channel.getId().equals(channelId)))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }
}
