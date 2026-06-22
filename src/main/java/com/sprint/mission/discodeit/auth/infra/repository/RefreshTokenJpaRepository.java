package com.sprint.mission.discodeit.auth.infra.repository;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.auth.domain.repository.RefreshTokenRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends RefreshTokenRepository,
    JpaRepository<RefreshToken, UUID> {

  @Override
  @EntityGraph(attributePaths = {"user"})
  Optional<RefreshToken> findByUser_Id(UUID userId);
}
