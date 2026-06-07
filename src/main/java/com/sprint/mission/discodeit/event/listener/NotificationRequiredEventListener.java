package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationRequiredEventListener {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final CacheManager cacheManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("ioTaskExecutor")
    public void handleMessageCreate(MessageCreatedEvent event){
        try{
            List<ReadStatus> activeStatus = readStatusRepository.findAllByChannelIdAndNotificationEnabledTrue(event.getChannelId());
            List<Notification> notifications = new ArrayList<>();

            Cache notificationCache = cacheManager.getCache("userNotifications");

            for(ReadStatus status : activeStatus){
                UUID subscriberId = status.getUser().getId();

                if(subscriberId.equals(event.getSenderId())) continue;
                User receiverProxy = userRepository.getReferenceById(subscriberId);
                notifications.add(new Notification(receiverProxy,
                        String.format("%s(#%s)", event.getSenderName(), event.getChannelName()),
                        event.getContent()));

                if(notificationCache != null){
                    notificationCache.evict(subscriberId);
                }
            }

            if(!notifications.isEmpty()){
                notificationRepository.saveAll(notifications);
            }
            log.info("메시지 알림 전송 완료: {} 건", notifications.size());
        } catch (Exception e){
            log.error("알림 전송 실패",e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("ioTaskExecutor")
    @CacheEvict(value = "userNotifications", key = "#event.receiverId")
    public void handleRoleUpdate(RoleUpdatedEvent event){
        try {
            String content = String.format("%s -> %s", event.getOldRole(), event.getNewRole());
            User receiverProxy = userRepository.getReferenceById(event.getReceiverId());
            log.info("권한 업데이트 알림 발송 시작");
            notificationRepository.save(new Notification(
                    receiverProxy,
                    "권한이 변경되었습니다.",
                    content));

            log.info("권한 업데이트 알림 발송 완료");
        } catch (Exception e){
            log.error("알림 전송 실패",e);
        }
    }
}
