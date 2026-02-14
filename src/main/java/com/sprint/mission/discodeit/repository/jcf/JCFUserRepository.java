package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userDB = new HashMap<>();

    // Create, Update
    @Override
    public User save(User user) {
        userDB.put(user.getId(), user);
        return user;
    }

    // Read
    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userDB.get(id));
    }
    @Override
    public List<User> findAll() {
        return new ArrayList<>(userDB.values());
    }
    // 유저의 존재 여부만을 확인
    @Override
    public boolean existsByUsername(String username) {
        return userDB.values().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }

    // Delete
    @Override
    public void delete(UUID id) {
        userDB.remove(id);
    }
}