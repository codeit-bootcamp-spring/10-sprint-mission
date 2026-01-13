package com.sprint.mission.descodeit.service;
import com.sprint.mission.descodeit.entity.User;

import java.util.*;
import java.util.UUID;

public interface UserService {
    User create(String name);
    User findUser(UUID userId);
    List<User> findAllUsers();
    User update(UUID userId, String newName);
    void delete(UUID userId);
}
