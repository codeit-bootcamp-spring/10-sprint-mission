package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {

    User create(String name);

    User read(UUID id);

    List<User> readAll();

    User updateUserName(UUID id, String name);

    void delete(UUID id);
}