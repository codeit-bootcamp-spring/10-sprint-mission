package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final List<User> data = new ArrayList<>();

    @Override
    public Optional<User> findById(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();
    }

    @Override
    public Optional<User> findByName(String userName) {
        return data.stream()
                .filter(u -> u.getUserName().equals(userName))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data);
    }

    @Override
    public List<User> findAllByChannelId(UUID channelId) {
        return data.stream()
                .filter(u -> u.getChannelIds()
                        .stream()
                        .anyMatch(findChannelId -> findChannelId.equals(channelId)))
                .toList();
    }

    @Override
    public void save(User user) {
        data.add(user);
    }

    @Override
    public void deleteById(UUID userId) {
        data.removeIf(u -> u.getId().equals(userId));
    }
}
