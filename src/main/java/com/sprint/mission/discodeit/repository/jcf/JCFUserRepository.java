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
        data.removeIf(u -> u.getId().equals(user.getId()));
        data.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(u -> u.getId().equals(id));
    }
}
