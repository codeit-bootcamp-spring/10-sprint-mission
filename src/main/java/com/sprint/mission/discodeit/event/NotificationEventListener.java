package com.sprint.mission.discodeit.event;

import org.springframework.cache.Cache;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserRepository userRepository;
    private final CacheManager cacheManager;

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent event) {
        readStatusRepository.findAllByChannelIdWithUser(event.getChannelId()).stream()
                .filter(rs -> rs.isNotificationEnabled())
                .filter(rs -> !rs.getUser().getId().equals(event.getAuthorId()))
                .forEach(rs -> {
                    String title = event.getAuthorUsername()
                            + " (#" + event.getChannelName() + ")";
                    String content = event.getContent();

                    notificationRepository.save(
                            new Notification(rs.getUser(), title, content)
                    );

                    // 알림 생성 시 캐시 무효화
                    Cache cache = cacheManager.getCache("notifications");
                    if (cache != null) {
                        cache.evict(rs.getUser().getId());
                    }
                    log.debug("메시지 알림 생성: receiverId={}", rs.getUser().getId());
                });
    }

    @Async
    @Transactional
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent event) {
        userRepository.findById(event.getUserId()).ifPresent(user -> {
            String title = "권한이 변경되었습니다.";
            String content = event.getOldRole().name() + " -> " + event.getNewRole().name();

            notificationRepository.save(new Notification(user, title, content));

            // 알림 생성 시 캐시 무효화
            Cache cache = cacheManager.getCache("notifications");
            if (cache != null) {
                cache.evict(event.getUserId());
            }
            log.debug("권한 변경 알림 생성: userId={}", event.getUserId());
        });
    }
}
