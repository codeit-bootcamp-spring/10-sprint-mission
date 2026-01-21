package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User saveUser(User user);
    User findUserById(UUID userId);
    List<User> findAll();
    void deleteUser(UUID userId);
}
