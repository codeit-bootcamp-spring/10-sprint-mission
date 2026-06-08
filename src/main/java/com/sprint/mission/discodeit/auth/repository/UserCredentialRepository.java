package com.sprint.mission.discodeit.auth.repository;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

  @EntityGraph(attributePaths = {"user"})
  Optional<UserCredential> findByUser_Email(String email);
}
