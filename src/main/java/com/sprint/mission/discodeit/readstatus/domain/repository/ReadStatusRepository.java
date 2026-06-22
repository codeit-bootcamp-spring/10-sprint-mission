package com.sprint.mission.discodeit.readstatus.domain.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ReadStatusRepository extends DomainRepository<ReadStatus> {

  boolean existsByChannel_IdAndUser_Id(UUID userId, UUID channelId);

  List<ReadStatus> findAllByUser_Id(UUID userId);

  long countByChannel_IdAndUser_IdIn(UUID channelId, Collection<UUID> userIds);
}
