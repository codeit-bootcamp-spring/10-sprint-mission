package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import java.util.List;


public interface UserService {
    void create(User user);

    User readById(UUID id);

    List<User> readAll();

    void update(User user);

    void delete(UUID id);
}
