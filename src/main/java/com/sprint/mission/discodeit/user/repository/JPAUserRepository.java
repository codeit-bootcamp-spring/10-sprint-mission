package com.sprint.mission.discodeit.user.repository;

import com.sprint.mission.discodeit.user.Role;
import com.sprint.mission.discodeit.user.entity.User;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPAUserRepository extends JpaRepository<User, UUID> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(Role role);

  Optional<User> findByEmail(String username);

}
