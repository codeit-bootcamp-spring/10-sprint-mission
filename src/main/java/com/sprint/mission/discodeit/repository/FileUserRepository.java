package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface FileUserRepository {
    User save(User user);
    User findById(UUID id);
    List<User> findAll();
    void deleteById(UUID id);
}