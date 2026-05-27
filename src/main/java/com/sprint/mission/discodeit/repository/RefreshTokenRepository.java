package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  Optional<RefreshToken> findByToken(String token);

  Optional<RefreshToken> findByUserId(UUID userId);

  void deleteByToken(String token);
}
