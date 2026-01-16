package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String username);

    List<User> getUserList();

    List<User> getUsersByChannel(UUID channelId);

    User getUserInfoByUserId(UUID userId);

    User updateUserName(UUID userId, String newName);

    void deleteUser(UUID userId);
}
