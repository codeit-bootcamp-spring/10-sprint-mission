package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User signUp(String userName, String email, String password);
    User signIn(String email, String password);
    User updateInfo(UUID uuid, String userName, String email, String password);
    User findUserById(UUID id);
    List<User> findAllUsers();
    void removeUserById(UUID id);
}
