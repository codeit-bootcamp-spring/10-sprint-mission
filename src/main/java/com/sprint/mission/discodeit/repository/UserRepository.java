package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.enums.Role;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);

  List<User> findAllByRole(Role role);

  @EntityGraph(attributePaths = {"profile"})
  List<User> findAll();

  Optional<User> findByUsername(String username);

  Optional<User> findByUsernameAndPassword(String username, String password);

  boolean existsByUsernameAndIdNot(String username, UUID id);

  boolean existsByEmailAndIdNot(String email, UUID id);
}
