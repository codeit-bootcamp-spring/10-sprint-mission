package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User CreateUser(String userName, String email);

    User findId(UUID userId);
    User findEmail(String email);
    List<User> findAll();

    User updateName(UUID userId, String userName);
    User updateEmail(UUID userId, String email);

    void delete(UUID userId);

    void addMessage(UUID userId, Message msg);
    List<Message> findMessages(UUID userId);
    List<Message> findMessagesInChannel(UUID userId, UUID channelId);
    void addChannel(UUID userId, Channel channel);
    List<Channel> findChannels(UUID userId);

}
