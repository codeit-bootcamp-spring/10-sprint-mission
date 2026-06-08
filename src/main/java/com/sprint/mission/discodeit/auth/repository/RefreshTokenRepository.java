package com.sprint.mission.discodeit.auth.repository;

import com.sprint.mission.discodeit.auth.domain.entity.RefreshToken;
import com.sprint.mission.discodeit.user.entity.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

  @EntityGraph(attributePaths = {"user"})
  Optional<RefreshToken> findByUser_Id(UUID userId);

  void deleteAllByUser(User user);
}
