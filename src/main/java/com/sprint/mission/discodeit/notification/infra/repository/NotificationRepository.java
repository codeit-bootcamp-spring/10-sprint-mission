package com.sprint.mission.discodeit.notification.infra.repository;

import com.sprint.mission.discodeit.common.jpa.repository.DomainRepository;
import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;

public interface NotificationRepository extends DomainRepository<Notification> {

  @EntityGraph(attributePaths = {"receiver"})
  List<Notification> findAllByReceiver_Id(UUID receiverId);
}
