package com.sprint.mission.service;

import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String nickName);

    User findById(UUID id);

    List<User> findAll();

    User updateUser(UUID id, String nickName);

    void deleteById(UUID id);
}
