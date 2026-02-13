package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String name);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);
}
