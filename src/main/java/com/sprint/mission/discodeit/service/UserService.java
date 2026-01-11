package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User signUp(String UserName, String email, String password);
    User updateName(UUID uuid, String name);
    User getUserById(UUID id);
    List<User> findAllUsers();
    List<Channel> getChannels(UUID id);
    void removeUserById(UUID id);
}
