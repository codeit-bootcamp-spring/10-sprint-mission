package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String name, String email);
    User findUserById(UUID userId);
    List<User> findAll();
    User updateUserNickname(UUID userId, String nickname);
    void deleteUser(UUID userId);
    List<Channel> getJoinedChannels(UUID userId);
    void removeChannelFromJoinedUsers(Channel channel);
}