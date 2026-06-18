package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.NotificationsCreatedEvent;
import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ReadStatusRepository readStatusRepository;
    private final CacheManager cacheManager;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public List<NotificationDto> createNotification(MessageCreatedEvent event){
        List<ReadStatus> activeStatus = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.getChannelId());
        List<NotificationDto> notificationDtos = new ArrayList<>();
        List<UUID> receiversId = new ArrayList<>();

        Cache notificationCache = cacheManager.getCache("userNotifications");

        for(ReadStatus status : activeStatus){
            UUID subscriberId = status.getUser().getId();
            receiversId.add(subscriberId);

            if(subscriberId.equals(event.getSenderId())) continue;
            User receiverProxy = userRepository.getReferenceById(subscriberId);
            Notification notification = notificationRepository.save(new Notification(receiverProxy,
                    String.format("%s(#%s)", event.getSenderName(), event.getChannelName()),
                    event.getContent()));

            notificationDtos.add(notificationMapper.toDto(notification));

            if(notificationCache != null){
                notificationCache.evict(subscriberId);
            }
        }

        eventPublisher.publishEvent(new NotificationsCreatedEvent(notificationDtos));

        return notificationDtos;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "userNotifications", key = "#userId")
    public List<NotificationDto> findNotifications(UUID userId){

        List<Notification> notifications = notificationRepository.findAllByReceiver_Id(userId);

        return notifications.stream()
                .map(notificationMapper::toDto)
                .toList();
    }

    @Transactional
    @CacheEvict(value = "userNotifications", key = "#receiverId")
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
