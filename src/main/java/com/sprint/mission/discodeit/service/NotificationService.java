package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "userNotifications", key = "#userId")
    public List<NotificationDto> findNotifications(UUID userId){

        List<Notification> notifications = notificationRepository.findAllByReceiver_Id(userId);

        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional
    public void readNotifications(UUID notificationId, UUID receiverId){
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(
                        () -> new DiscodeitException(ErrorCode.NOTIFICATION_NOT_FOUND, Map.of(
                                "notificationId" , notificationId
                        ))
                );

        if(!notification.getReceiver().getId().equals(receiverId)){
            throw new DiscodeitException(ErrorCode.FORBIDDEN_ACCESS, Map.of());
        }
        notificationRepository.delete(notification);

    }
}
