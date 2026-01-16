package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(String username, String email, String password);
    User findUserById(UUID userId);
    User findUserByEmail(String email);
    List<User> findUsersByChannel(UUID channelId);
    List<User> findAllUser();
    User update(UUID userId, String password, String username, String email);
    User updatePassword(UUID userId, String currentPassword, String newPassword);
    void delete(UUID userId, String password);
    void saveOrUpdate(User user);
}
