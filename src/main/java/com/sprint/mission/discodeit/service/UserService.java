package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User CreateUser(String userName, String email);

    User findById(UUID userId);
    User findByEmail(String email);
    List<User> findAll();

    User update(UUID userId, String userName, String email);

    void delete(UUID userId);

    void addMessage(UUID userId, Message msg);
    void addChannel(UUID userId, Channel channel);


    void deleteAll();
}
