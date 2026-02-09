package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends DomainRepository<User> {
    boolean existsByUsername(String username) throws IOException;
    boolean existsByEmail(String email) throws IOException;
    List<User> findAll() throws IOException, ClassNotFoundException;
    Optional<User> findByUsernameAndEmail(String username, String email);
}
