package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> find(UUID userID);
    List<User> findAll();
    void deleteUser(User user);
    User save(User user);
}
