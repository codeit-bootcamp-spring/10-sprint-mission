package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String username, String password, String email);

    Optional<User> findUserById(UUID id);

    List<User> findAllUsers();

    User updateUserInfo(UUID id, String newUsername, String newEmail);

    User changePassword(UUID id, String newPassword);

    void deleteUser(UUID id);

}
