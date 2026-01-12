package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String userName, String password, String email);
    User getUser(UUID userId);
    List<User> getAllUsers();
    User updateUser(User user);
    User deleteUser(User user);
}
