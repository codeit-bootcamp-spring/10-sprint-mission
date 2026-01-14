package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

public interface UserService {
    void createUser(User user);

    void sendMessage(User user, Message message);

    void changeUser(User oldUser, User newUser);

    void deleteUser(User user);
}
