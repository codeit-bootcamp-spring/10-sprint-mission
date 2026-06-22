package com.sprint.mission.discodeit.notification.infra.repository;

import com.sprint.mission.discodeit.notification.domain.entity.Notification;
import com.sprint.mission.discodeit.notification.domain.repository.NotificationRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends NotificationRepository,
    JpaRepository<Notification, UUID> {

  @Override
  @EntityGraph(attributePaths = {"receiver"})
  List<Notification> findAllByReceiver_Id(UUID receiverId);
}
