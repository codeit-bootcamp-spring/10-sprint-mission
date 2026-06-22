package com.sprint.mission.discodeit.readstatus.domain.repository;

import java.util.List;
import java.util.UUID;

public interface ReadStatusReferenceRepository {

  List<UUID> findUserIdsByChannel_IdAndNotificationEnabledIsTrue(UUID channelId);

  boolean existsByChannel_IdAndUser_Id(UUID userId, UUID channelId);
}
