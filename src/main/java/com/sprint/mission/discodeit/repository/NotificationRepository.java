package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
    // 특정 사용자의 알림 목록 조회 (내림차순)
    List<NotificationEntity> findAllByReceiverIdOrderByCreatedAtDesc(UUID userId);
}
