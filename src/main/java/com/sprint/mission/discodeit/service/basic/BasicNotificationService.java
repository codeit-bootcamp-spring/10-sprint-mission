package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private final CacheManager cacheManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void create(Set<UUID> receiverIds, String title, String content) {
        log.debug("[NOTIFICATION_CREATE] 알림 생성 시작: title={}, content={}, count={}",
                title, content, receiverIds.size());

        List<Notification> notificationList = receiverIds.stream()
                .map(receiverId -> validateAndGetUserByUserId(receiverId))
                .map(receiver -> new Notification(receiver, title, content))
                .toList();

        notificationRepository.saveAll(notificationList);

        // 각 receiverId 별로 캐시 삭제
        evictNotificationListCache(receiverIds);

        log.info("[NOTIFICATION_CREATE] 알림 생성 완료: title={}, content={}, count={}",
                title, content, notificationList.size());
    }

    @Cacheable(value = "notificationList", key = "#receiverId", unless = "#result.isEmpty()")
    @Transactional(readOnly = true)
    @Override
    public List<NotificationDto> findAllByReceiverId(UUID receiverId) {
        log.debug("[NOTIFICATION_LIST_FIND] 알림 목록 조회 시작: receiverId={}", receiverId);

        // 사용자(receiver) 검증
        validateAndGetUserByUserId(receiverId);
        
        List<NotificationDto> notificationDtoList = notificationRepository
                .findAllByReceiverIdOrderByCreatedAtDesc(receiverId)
                .stream()
                .map(notification -> notificationMapper.toDto(notification))
                .toList();
        
        log.debug("[NOTIFICATION_LIST_FIND] 알림 목록 조회 완료: count={}", notificationDtoList.size());

        return notificationDtoList;
    }

    @CacheEvict(value = "notificationList", key = "#receiverId")
    @PreAuthorize("#receiverId != null and #receiverId.equals(authentication.principal.userDto.id)")
    @Override
    public void deleteByReceiverId(UUID receiverId, UUID notificationId) {
        log.debug("[NOTIFICATION_CHECK] 알림 확인 시작: receiverId={}, notificationId={}",
                receiverId, notificationId);

        // 사용자(receiver) 검증
        validateAndGetUserByUserId(receiverId);

        // 알림 검증
        Notification notification = validateAndGetNotificationByUserId(notificationId);

        // 해당 알림의 수취인이 user가 맞는지 확인
        UUID notificationReceiverId = notification.getReceiver().getId();
        if (!notificationReceiverId.equals(receiverId)) {
            throw new AccessDeniedException("본인 알림만 삭제할 수 있습니다.");
        }

        // 알림 확인(삭제)
        notificationRepository.delete(notification);

        log.debug("[NOTIFICATION_CHECK] 알림 확인 완료: receiverId={}, notificationId={}",
                receiverId, notificationId);
    }

    // 사용자(receiver) 검증
    private User validateAndGetUserByUserId(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException("userId", null);
        }
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

    // 알림 검증
    private Notification validateAndGetNotificationByUserId(UUID notificationId) {
        if (notificationId == null) {
            throw new InvalidInputException("notificationId", null);
        }

        return notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new NotificationNotFoundException("notificationId", notificationId)
                );
    }

    // 각 receiverId 별로 캐시 삭제
    private void evictNotificationListCache(Set<UUID> receiverIds) {
        Cache cache = cacheManager.getCache("notificationList");

        if (cache == null) {
            return;
        }

        receiverIds.stream()
                .filter(receiverId -> receiverId != null)
                .forEach(receiverId -> cache.evict(receiverId));
    }
}
