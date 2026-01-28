package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // CRUD
    User create(String name);
    User find(UUID id);
    List<User> findAll();
    User updateName(UUID userID, String name);
    default void update() {}
    void deleteUser(UUID userID);

    List<String> findJoinedChannels(UUID userID);
}
