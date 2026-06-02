package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

  @EntityGraph(attributePaths = {"status", "profile"})
  Optional<User> findById(@NonNull UUID userId);

  @EntityGraph(attributePaths = {"status", "profile"})
  List<User> findAll();

  @EntityGraph(attributePaths = {"status", "profile"})
  Optional<User> findByUsername(String username);

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);
}
