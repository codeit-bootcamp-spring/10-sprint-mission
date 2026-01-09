package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface UserService {
    UUID createUser(String username, String email);
    User getUser(UUID id);
    Iterable<User> getAllUsers();
    void updateUser(UUID id, String username, String email);
    void deleteUser(UUID id);
}
