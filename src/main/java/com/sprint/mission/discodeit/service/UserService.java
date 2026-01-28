package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String userName, String userEmail);

    User readUser(UUID id);

    List<User> readAllUser();

    User updateUser(UUID id, String userName, String userEmail);

    void deleteUser(UUID id);

    List<User> readUsersByChannel(UUID channelId);



}
