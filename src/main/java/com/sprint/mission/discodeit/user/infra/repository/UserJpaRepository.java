package com.sprint.mission.discodeit.user.infra.repository;

import com.sprint.mission.discodeit.user.domain.entity.User;
import com.sprint.mission.discodeit.user.domain.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends UserRepository, JpaRepository<User, UUID> {

  @Override
  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findProfileById(UUID id);

  @Override
  @EntityGraph(attributePaths = {"profile"})
  List<User> findAllUsersProfileBy();
}
