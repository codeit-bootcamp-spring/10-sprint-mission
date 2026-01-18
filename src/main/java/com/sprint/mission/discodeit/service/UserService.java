package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;

import java.util.*;

public interface UserService{
    User createUser(String username);

    User findUserByUserId(UUID id);
    //  User findUserByUsername(String username);
    List<User> findAllUsers();

    User updateUser(UUID id, String username);

    void deleteUser(UUID id);
}