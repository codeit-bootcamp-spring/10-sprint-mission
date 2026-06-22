package com.sprint.mission.discodeit.readstatus.infra.repository;

import com.sprint.mission.discodeit.readstatus.domain.entity.ReadStatus;
import com.sprint.mission.discodeit.readstatus.domain.repository.ReadStatusRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadStatusJpaRepository extends ReadStatusRepository,
    JpaRepository<ReadStatus, UUID> {

}
