package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(UUID id);
    List<User> findAll();
    boolean existsById(UUID id);
    void deleteById(UUID id);

    //검증용
    boolean existsByUsername(String username);
    boolean existsByPassword(String password);
    boolean existsByEmail(String email);
}
