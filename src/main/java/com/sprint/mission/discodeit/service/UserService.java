package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    void createUser(User user);

    void sendMessage(User user, Message message);

    void changeUser(User oldUser, User newUser);

    void deleteUser(User user);

    List<UUID> getMessageUUIDs(User user);

    List<UUID> getChannelUUIDs(User user);

    void deleteChannelFromUsers(Channel channel, List<UUID> userUUIDs);

    void addChannel(User user, Channel channel);

    void deleteMessage(User user, Message message);
}
