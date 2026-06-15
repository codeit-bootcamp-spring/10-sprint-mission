package com.sprint.mission.discodeit.user.infra.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.entity.constant.UserRole;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UserRepository extends DomainRepository<User> {

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);

  boolean existsByRole(UserRole role);

  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findProfileById(UUID id);

  @EntityGraph(attributePaths = {"profile"})
  List<User> findAllUsersProfileBy();

  @EntityGraph(attributePaths = {"profile"})
  List<User> findProfileByIdIn(Collection<UUID> id);
}
