package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String userName, String password, String email);
    User getUser(UUID userId);
    List<User> getAllUsers();
    List<Message> getMessagesById(UUID userId);
    List<Channel> getChannelsById(UUID userId);
    User updateUser(UUID userId, String userName, String password, String email);
    void deleteUser(UUID userId);
}
