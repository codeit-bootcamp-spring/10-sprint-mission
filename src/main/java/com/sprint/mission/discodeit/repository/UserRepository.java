package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.util.List;

public interface UserRepository extends DomainRepository<User> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findAll();
}
