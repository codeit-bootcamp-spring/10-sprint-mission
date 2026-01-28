package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    User findUserById(UUID userId);
    List<User> findAllUser();
    void delete(User user);
}
