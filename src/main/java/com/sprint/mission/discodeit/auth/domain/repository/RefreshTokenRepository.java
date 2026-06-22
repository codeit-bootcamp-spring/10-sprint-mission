package com.sprint.mission.discodeit.auth.domain.repository;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends DomainRepository<RefreshToken> {

  Optional<RefreshToken> findByUser_Id(UUID userId);
}
