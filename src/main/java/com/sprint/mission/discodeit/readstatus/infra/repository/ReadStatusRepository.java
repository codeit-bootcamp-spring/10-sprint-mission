package com.sprint.mission.discodeit.readstatus.infra.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusRepository extends DomainRepository<ReadStatus> {

  boolean existsByChannel_IdAndUser_Id(UUID userId, UUID channelId);

  List<ReadStatus> findAllByUserId(UUID userId);

  long countByChannel_IdAndUser_IdIn(UUID channelId, Collection<UUID> userIds);

  @Query("SELECT r.user.id FROM ReadStatus r WHERE r.channel.id = :channelId AND r.notificationEnabled = true")
  List<UUID> findUserIdsByChannel_IdAndNotificationEnabledIsTrue(UUID channelId);
}
