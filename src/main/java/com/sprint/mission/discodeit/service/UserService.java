package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.UUID;

public interface UserService {
<<<<<<< HEAD
    User create(String username, String email, String password);
    User find(UUID userId);
    List<User> findAll();
    User update(UUID userId, String newUsername, String newEmail, String newPassword);
    void delete(UUID userId);
=======

    User createUser(String username, String email);
    User findById(UUID id);
    List<User> findAll();

    User update(UUID id, String username, String email);

    void delete(UUID id);
>>>>>>> upstream/김혜성
}
