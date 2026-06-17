package com.sprint.mission.discodeit.notification.repository;

import com.sprint.mission.discodeit.notification.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JPANotificationRepository extends JpaRepository<Notification, UUID> {

  List<Notification> findAllByReceiverId(UUID receiverId);

  void deleteNotificationById(UUID id);
}
