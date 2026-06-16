package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  // 유저 ID로 조회하되, 최신순으로 가져옵니다.
  List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);

  Optional<Notification> findByIdAndReceiverId(UUID id, UUID receiverId);

  boolean existsById(UUID id);
}