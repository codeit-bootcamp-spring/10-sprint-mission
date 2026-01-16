package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import java.util.List;


public interface UserService {
    User create(String name, String nickname, String email, String password);

    User findById(UUID id);

    List<User> findAll();

    User update(UUID id, String name, String nickname, String email);

    User updateStatus(UUID id, String status);

    User updatePassword(UUID id, String newPassword);

    void delete(UUID id);
}
