package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    public User save(User user);

    public Optional<User> findById(UUID id);

    public Optional<User> findByName(String name);

    public Optional<User> findByEmail(String email);

    public List<User> readAll();

    public void delete(UUID id);

    public void clear();

}
