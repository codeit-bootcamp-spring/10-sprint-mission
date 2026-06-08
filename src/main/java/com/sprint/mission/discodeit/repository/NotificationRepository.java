package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  @Query("SELECT new com.sprint.mission.discodeit.dto.data.NotificationDto("
      + "n.id, n.createdAt, n.receiver.id, n.title, n.content) "
      + "FROM Notification n "
      + "WHERE n.receiver.id = :receiverId "
      + "ORDER BY n.createdAt DESC")
  List<NotificationDto> findAllDtoByReceiverId(@Param("receiverId") UUID receiverId);

  boolean existsByIdAndReceiverId(UUID notificationId, UUID receiverId);
}
