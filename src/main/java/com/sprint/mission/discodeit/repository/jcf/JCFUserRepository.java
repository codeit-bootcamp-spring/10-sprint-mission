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
    public User save(User user) {
        this.data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(this.data.get(id));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return this.data.values().stream()
                .filter(user -> user.getUsername().equals(username)).findFirst();
    }

    @Override
    public Optional<User> findByEmail(String Email){
        return this.data.values().stream()
                .filter(user -> user.getEmail().equals(Email)).findFirst();
    }

    @Override
    public List<User> findAll() {
        return this.data.values().stream().toList();
    }

    @Override
    public boolean existsById(UUID id) {
        return this.data.containsKey(id);
    }

    @Override
    public boolean existByUsername(String username){
        return this.data.values().stream().anyMatch(user -> user.getUsername().equals(username));
    }

    @Override
    public boolean existByEmail(String email){
        return this.data.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public void deleteById(UUID id) {
        this.data.remove(id);
    }
}
