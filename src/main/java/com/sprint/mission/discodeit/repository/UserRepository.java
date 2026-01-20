package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User find(UUID userID);
    List<User> findAll();
    void addUser(User user);
    void removeUser(User user);
    User save(User user);
}
