package com.sprint.mission.descodeit.service;
import com.sprint.mission.descodeit.entity.User;

import java.util.Set;
import java.util.UUID;

public interface UserService {
    void create(User user);
    User findUser(UUID userID);
    Set<UUID> findAllUsers();
    void update(UUID userID, String neName);
    void delete(UUID userID);
}
