package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(User user);

    List<User> getUserList();

    User getUserInfoByUserId(UUID userId);

    User updateUserName(UUID userId, String newName);

    void deleteUser(UUID userId);
}
