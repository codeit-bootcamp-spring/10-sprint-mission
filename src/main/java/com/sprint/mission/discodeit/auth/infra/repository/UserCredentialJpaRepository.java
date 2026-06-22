package com.sprint.mission.discodeit.auth.infra.repository;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.auth.domain.repository.UserCredentialRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialJpaRepository extends UserCredentialRepository,
    JpaRepository<UserCredential, UUID> {

  @Override
  @EntityGraph(attributePaths = {"user", "user.profile"})
  Optional<UserCredential> findByUser_Id(UUID userId);

  @Override
  @EntityGraph(attributePaths = {"user"})
  Optional<UserCredential> findByUser_Email(String email);
}
