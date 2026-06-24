package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.entity.NotificationEntity;
import com.sprint.mission.discodeit.entity.ReadStatusEntity;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.UUID;

/*
    NotificationEventListener
    -------------------------
    메인 트랜잭션이 성공적으로 커밋됐을 때만, 알림을 발행하는 리스너
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationRepository notificationRepository;

    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(MessageCreatedEvent messageCreatedEvent) {
        // 1. 해당 채널의 알림이 활성화된 ReadStatus 조회
        List<ReadStatusEntity> readStatuses = readStatusRepository.findByChannelIdAndNotificationEnabledTrue(messageCreatedEvent.channelId());

        // 2. 작성자를 제외한 알림 엔티티 생성
        List<NotificationEntity> notifications = readStatuses.stream()
                .filter(status -> !status.getUser().getId().equals(messageCreatedEvent.senderId()))
                .map(status -> new NotificationEntity(
                        status.getUser(),
                        String.format("%s (#%s)", messageCreatedEvent.senderName(), messageCreatedEvent.channelName()),
                        messageCreatedEvent.content()
                ))
                .toList();

        // 3. DB에 일괄 저장
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);

            // 수신자 캐시 무효화
            notifications.forEach(notification ->
                    cacheManager.getCache("notifications").evict(notification.getReceiver().getId())
            );
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(RoleUpdatedEvent roleUpdatedEvent) {
        UserEntity targetUser = getUserEntityOrThrow(roleUpdatedEvent.userId());

        NotificationEntity notification = new NotificationEntity(
                targetUser,
                "권한이 변경되었습니다.",
                String.format("%s -> %s", roleUpdatedEvent.oldRole(), roleUpdatedEvent.newRole())
        );

        notificationRepository.save(notification);
        cacheManager.getCache("notifications").evict(notification.getReceiver().getId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(S3UploadFailedEvent s3UploadFailedEvent) {
        UserEntity targetUser = getUserEntityOrThrow(UUID.fromString(s3UploadFailedEvent.requestId()));

        NotificationEntity notification = new NotificationEntity(
                targetUser,
                "S3 파일 업로드 실패",
                String.format("RequestId:\n%s", s3UploadFailedEvent.requestId())
        );

        notificationRepository.save(notification);
        cacheManager.getCache("notifications").evict(notification.getReceiver().getId());
    }

    private UserEntity getUserEntityOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
