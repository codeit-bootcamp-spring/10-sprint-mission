package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String Email);

    List<User> findAll();
    boolean existsById(UUID id);

    boolean existByUsername(String username);

    boolean existByEmail(String email);

    void deleteById(UUID id);
}
