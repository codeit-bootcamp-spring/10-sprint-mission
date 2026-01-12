package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username);
    User findById(UUID userId);
    List<User> findAll();
    void update(UUID userId, String username);
    void delete(UUID userId);
}
