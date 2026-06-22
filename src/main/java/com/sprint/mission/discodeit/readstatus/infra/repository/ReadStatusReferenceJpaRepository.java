package com.sprint.mission.discodeit.readstatus.infra.repository;

import com.sprint.mission.discodeit.common.jpa.repository.EntityReferenceJpaRepository;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.repository.ReadStatusReferenceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface ReadStatusReferenceJpaRepository extends ReadStatusReferenceRepository,
    EntityReferenceJpaRepository<ReadStatus> {

  @Override
  @Query("SELECT r.user.id FROM ReadStatus r WHERE r.channel.id = :channelId AND r.notificationEnabled = true")
  List<UUID> findUserIdsByChannel_IdAndNotificationEnabledIsTrue(UUID channelId);
}
