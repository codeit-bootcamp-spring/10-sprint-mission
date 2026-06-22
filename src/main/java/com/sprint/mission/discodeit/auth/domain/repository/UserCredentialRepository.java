package com.sprint.mission.discodeit.auth.domain.repository;

import com.sprint.mission.discodeit.auth.domain.entity.UserCredential;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends DomainRepository<UserCredential> {

  Optional<UserCredential> findByUser_Id(UUID userId);

  Optional<UserCredential> findByUser_Email(String email);
}
