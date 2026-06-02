package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final NotificationRepository notificationRepository;
    private final ReadStatusRepository readStatusRepository;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void on(MessageCreatedEvent event) {
        Message message = event.message();
        Channel channel = message.getChannel();
        User sender = message.getAuthor();

        List<ReadStatus> readStatuses =
                readStatusRepository.findByChannelAndNotificationEnabledTrue(channel);

        log.info("[NOTIFICATION_EVENT] channelId={}, senderId={}, targetCount={}",
                channel.getId(), sender.getId(), readStatuses.size());

        String title = sender.getUsername() + " (#" + channel.getName() + ")";
        String content = message.getContent();

        for (ReadStatus readStatus : readStatuses) {
            User receiver = readStatus.getUser();

            if (receiver.getId().equals(sender.getId())) {
                continue;
            }

            Notification notification = new Notification(receiver, title, content);
            notificationRepository.save(notification);

            log.info("[NOTIFICATION_CREATED] notificationId={}, receiverId={}, title={}",
                    notification.getId(), receiver.getId(), title);
        }
    }
}
