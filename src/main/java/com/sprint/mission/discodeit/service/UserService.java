package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User create(String name);

    Optional<User> read(UUID id);

    ArrayList<User> readAll();

    User update(UUID id, String name);

    void delete(UUID id);
}
