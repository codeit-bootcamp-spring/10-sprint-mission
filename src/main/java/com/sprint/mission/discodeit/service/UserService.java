package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    // Create
    User create(User user);

    // Read
    User read(UUID id);

    // ReadAll
    List<User> readAll();

    // Update
    User update(User user);

    // Delete
    void delete(UUID id);

    // Clear
    void clear();
}
