package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User createUser(String userName, String userEmail, String userPassword);

    User readUser(UUID id);

    List<User> readAllUser();

    void updateUser(UUID id, String userName, String userEmail, String userPassword);

    void deleteUser(UUID id);

    boolean isUserDeleted(UUID id);
}
