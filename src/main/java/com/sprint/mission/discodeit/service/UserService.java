package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void createUser(User user);

    List<User> getUserList();

    UUID getUserIdByName(String userName);

    void updateUserName(UUID userId, String newName);

    void deleteUser(UUID userId);

    void joinChannel(UUID userId, UUID channelId, String channelName);

    void listUserChannels(UUID userId);

    void leaveChannel(UUID userId, UUID channelId);
}
