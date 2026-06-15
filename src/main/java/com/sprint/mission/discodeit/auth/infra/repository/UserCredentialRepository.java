package com.sprint.mission.discodeit.auth.infra.repository;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

public interface UserCredentialRepository extends DomainRepository<UserCredential> {

  @EntityGraph(attributePaths = {"user", "user.profile"})
  Optional<UserCredential> findByUser_Id(UUID userId);

  @EntityGraph(attributePaths = {"user"})
  Optional<UserCredential> findByUser_Email(String email);
}
