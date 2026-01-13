package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Create
    User create(String name, UserStatus status);

    // Read
    User findById(UUID id);

    // ReadAll
    List<User> readAll();

    // Update
    User update(UUID id);

    // Delete
    void delete(UUID id);

}
