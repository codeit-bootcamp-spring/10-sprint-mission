package com.sprint.mission.descodeit.repository;

import com.sprint.mission.descodeit.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.Map;

public interface UserRepository {
    User findById(UUID userId);
    List<User> findAll();
    void save(UUID userId , User user);
    void delete(UUID userId);
}
