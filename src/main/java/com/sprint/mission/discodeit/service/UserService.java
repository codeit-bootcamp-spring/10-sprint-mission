package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(User user);

    List<User> getUserList();

    void updateUserName(UUID userId, String newName);

    void deleteUser(UUID userId);
}
