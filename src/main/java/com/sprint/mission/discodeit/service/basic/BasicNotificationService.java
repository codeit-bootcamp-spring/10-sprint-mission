package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Cacheable(cacheNames = "notifications", key = "#receiverId")
    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional
    @Override
    @CacheEvict(cacheNames = "notifications", key = "#requesterId")
    public void delete(UUID notificationId, UUID requesterId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> NotificationNotFoundException.withId(notificationId));

        if (!notification.getReceiver().getId().equals(requesterId)) {
            throw new AccessDeniedException("본인의 알림만 확인할 수 있습니다.");
        }

        notificationRepository.delete(notification);
    }
}

