package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {
    void create();

    Optional<User> read(UUID id);

    Optional<ArrayList<User>> readAll();

    void update(User userData);

    User delete(UUID id);
}
