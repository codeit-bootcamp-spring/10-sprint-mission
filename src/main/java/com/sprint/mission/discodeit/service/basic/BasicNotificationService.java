package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.entity.NotificationEntity;
import com.sprint.mission.discodeit.exception.notification.AccessDeniedNotificationException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

    private NotificationRepository notificationRepository;

    private NotificationMapper notificationMapper;

    // 알림 조회
    @Override
    public List<NotificationDto> findAll(UUID userId) {
        return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    // 알림 삭제
    @Override
    @CacheEvict(cacheNames = "notifications", key = "#requesterId")
    @Transactional
    public void delete(UUID notificationId, UUID userId) {
        NotificationEntity targetNotification = getNotificationEntityOrThrow(notificationId);

        // 403 에러 처리 (요청자 본인의 알림이 아닌 경우)
        if (!targetNotification.getReceiver().getId().equals(userId)) {
            throw new AccessDeniedNotificationException();
        }

        notificationRepository.delete(targetNotification);
    }

    // 알림 반환
    private NotificationEntity getNotificationEntityOrThrow(UUID notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));
    }
}
