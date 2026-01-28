package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(User user) {
        data.put(user.getId(), user);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return this.data.values().stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userId) {
        data.remove(userId);
    }

    @Override
    public boolean existByEmail(String newEmail) {
        return this.data.values().stream()
                .anyMatch(user -> user.getEmail().equals(newEmail));
    }

    @Override
    public boolean isEmailUsedByOther(UUID userId, String newEmail) {
        return this.data.values().stream()
                .anyMatch(user -> !user.getId().equals(userId) && user.getEmail().equals(newEmail));
    }
}
