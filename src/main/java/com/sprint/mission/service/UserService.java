package com.sprint.mission.service;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String name, String email);

    User findById(UUID id);

    List<Channel> findByUserId(UUID userId);

    List<User> findAll();

    User update(UUID id, String name);

    void deleteById(UUID id);
}
