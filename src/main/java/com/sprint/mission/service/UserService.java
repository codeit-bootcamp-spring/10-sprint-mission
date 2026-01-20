package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String name, String email);

//    User findById(UUID id);

//    List<Channel> findByUserId(UUID userId);

    List<User> getAllUser();

    User update(UUID userId, String name);

    User getUserOrThrow(UUID userId);

    void deleteUser(UUID userId);

    List<User> getChannelUsers(UUID channelId);
}
