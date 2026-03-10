package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByName(String name);

  boolean existsByEmail(String email);

  User findByName(String name);

  default void delete(UUID id) {
    deleteById(id);
  }
}