package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.ReadStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusRepository extends JpaRepository<ReadStatus, UUID> {

  boolean existsByUserIdAndChannelId(UUID userId, UUID channelId);

  Optional<ReadStatus> findByUserIdAndChannelId(UUID userId, UUID channelId);

  List<ReadStatus> findAllByChannelId(UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);
}
