package com.sprint.mission.discodeit.user.domain.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends DomainRepository<User> {

  Optional<User> findProfileById(UUID id);

  List<User> findAllUsersProfileBy();

  boolean existsByUsername(String username);

  boolean existsByEmail(String email);
}
