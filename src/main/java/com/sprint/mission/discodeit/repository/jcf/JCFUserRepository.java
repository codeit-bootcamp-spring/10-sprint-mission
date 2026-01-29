package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {

    private final List<User> data = new ArrayList<>();

    @Override
    public User save(User user) {
        delete(user);
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return data.stream()
                .filter(u -> u.getId().equals(userId))
                .findAny();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(User user) {
        data.removeIf(u -> u.equals(user));
    }
}
