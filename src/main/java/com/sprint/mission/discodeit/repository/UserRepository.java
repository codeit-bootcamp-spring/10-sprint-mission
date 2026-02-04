package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

public interface UserRepository extends DomainRepository<User> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
