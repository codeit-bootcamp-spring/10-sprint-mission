package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  @EntityGraph(attributePaths = "receiver")
  List<Notification> findAllByReceiverId(UUID receiverId);
}
