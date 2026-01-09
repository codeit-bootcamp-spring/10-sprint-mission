package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String username);
    Optional<User> findOne(UUID id);
    List<User> findAll();
    User updateUser(UUID id, String newUsername);
    void deleteUser(UUID id);
}