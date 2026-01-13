package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.User;

import java.util.*;

public interface UserService {
    void save(User user);

    Optional<User> findById(UUID id);

    List<User> findAll();

    void delete(User user);
}
