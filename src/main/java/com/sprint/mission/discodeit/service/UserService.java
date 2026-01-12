package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email);
    User findUserById(UUID userId);
    List<User> findAll();
    User updateUserNickname(UUID userId, String nickname);
    void deleteUser(UUID userId);
    Set<UUID> getJoinedChannels(UUID userId);
    void joinChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId, UUID userId);
    void leaveChannel(UUID channelId);
}