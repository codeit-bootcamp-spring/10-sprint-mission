package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.config.MDCLoggingInterceptor;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncFailureNotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final CacheManager cacheManager;
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyS3UploadFailed(UUID binaryContentId, Throwable exception) {
        String requestId = Optional.ofNullable(MDC.get(MDCLoggingInterceptor.REQUEST_ID))
                .orElse("unknown");

        String content = """
        Task: S3 파일 업로드
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """.formatted(requestId, binaryContentId, exception.getMessage());

        List<User> admins = userRepository.findAllByRole(Role.ADMIN);
        List<Notification> notifications = admins.stream()
                .map(admin -> new Notification(admin, "S3 파일 업로드 실패", content))
                .toList();

        if (notifications.isEmpty()) {
            log.warn("S3 업로드 실패 알림을 받을 ADMIN 계정이 없습니다: binaryContentId={}", binaryContentId);
            return;
        }

        notificationRepository.saveAll(notifications);
        admins.forEach(admin -> evictNotificationCache(admin.getId()));
    }

    private void evictNotificationCache(UUID receiverId) {
        Cache cache = cacheManager.getCache("notifications");
        if (cache != null) {
            cache.evict(receiverId);
        }
    }
}
