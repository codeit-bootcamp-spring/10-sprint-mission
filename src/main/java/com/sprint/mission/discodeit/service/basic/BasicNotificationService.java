package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;

    @Value("${discodeit.admin.username}")
    private String adminUsername;

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Override
    public void notifyAdmin(String title, String content) {
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new UserNotFoundException(adminUsername));

        Notification notification = new Notification(
                admin, title, content
        );

        notificationRepository.save(notification);

        log.warn("[ADMIN_NOTIFICATION_CREATE] 관리자 알림 생성: adminId={}, title={}",
                admin.getId(), title);
    }

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
    public void delete(UUID notificationId, UUID receiverId) {
        Notification notification = notificationRepository.findByIdAndReceiverId(notificationId, receiverId)
                .orElseThrow(() -> new NotificationNotFoundException(notificationId));

        log.info("[NOTIFICATION_DELETE_SUCCESS] 알림 삭제 성공: notificationId={}", notificationId);
        notificationRepository.delete(notification);
    }
}
