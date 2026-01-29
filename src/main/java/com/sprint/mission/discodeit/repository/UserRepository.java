package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserRepository {
    void save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    void delete(User user);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
