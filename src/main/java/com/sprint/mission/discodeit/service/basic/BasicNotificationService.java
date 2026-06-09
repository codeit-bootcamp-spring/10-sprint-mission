package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Cacheable(value = "notifications", key = "#receiverId")
    @Override
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        return notificationRepository.findAllByReceiverId(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    // 알림 확인 (삭제)
    @CacheEvict(value = "notifications", allEntries = true)
    @Transactional
    @Override
    public void delete(UUID notificationId, UUID requesterId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found: " + notificationId));

        // 본인 알림만 삭제 가능
        if (!notification.getReceiver().getId().equals(requesterId)) {
            throw new AccessDeniedException("본인의 알림만 확인할 수 있습니다.");
        }

        notificationRepository.deleteById(notificationId);

    }
}
