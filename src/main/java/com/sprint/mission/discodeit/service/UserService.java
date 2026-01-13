package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    User create(User user);
    User findById(UUID id);
    List<User> findAll();

    User update(UUID id, String username, String email);

    void delete(UUID id);
}
