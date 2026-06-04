package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  // 수신자 ID로 알림 목록 조회 (최신순 정렬)
  List<Notification> findAllByReceiverIdOrderByCreatedAtDesc(UUID receiverId);
}
