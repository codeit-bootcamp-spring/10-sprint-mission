package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserRepository {

    void save(User user);

    Optional<User> findById(UUID id);

    Optional<User> findByUsername(String username);

    List<User> findAll();

    void delete(User user);

    boolean existsById(UUID id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
