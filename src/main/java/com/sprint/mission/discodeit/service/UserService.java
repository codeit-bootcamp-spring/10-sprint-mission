package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {
    User createUser(String username, String email);
    User findUserById(UUID userId);
    User findUserByEmail(String email);
    List<User> findUsersByChannel(UUID channelId);
    List<User> findAllUser();
    User updateUser(UUID userId, String username, String email);
    void deleteUser(UUID userId);
}
