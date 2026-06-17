package com.sprint.mission.discodeit.event.listener;

import com.sprint.mission.discodeit.config.init.AdminProperties;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// MessageCreatedEvent를 받아 메시지 생성 시 알림을 발행하는 Listener
//@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationRequiredEventListener {

    private final UserRepository userRepository;
    private final ReadStatusRepository readStatusRepository;
    private final NotificationService notificationService;

    // 관리자 Username
    private final AdminProperties adminProperties;

    // 채널에 새로운 메시지 생성 시 알림을 설정한 모든 참가자에게 알림을 보내는 Listener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async(value = "eventTaskExecutor")
    public void on(MessageCreatedEvent event) {

        // 채널 알림 여부를 활성화(true)한 ReadStatus 조회한 후 사용자 ID Set(중복 방지)
        Set<UUID> receiverIds = readStatusRepository
                .findAllByChannelIdAndNotificationEnabledIsTrue(event.getChannelId())
                .stream()
                .map(readStatus -> readStatus.getUser().getId())
                // 메시지 author는 제외
                .filter(userId -> !userId.equals(event.getAuthorId()))
                .collect(Collectors.toSet());

        // title
        String title = event.getChannelType().equals(ChannelType.PUBLIC)
                ? String.format("%s (#%s)", event.getAuthorName(), event.getChannelName())
                : event.getAuthorName();

        // 메시지 내용 (content)
        String content = event.getMessageContent();

        // 해당 정보를 notificationService로 전송해 알림 생성
        notificationService.create(receiverIds, title, content);
    }

    // 권한(role)이 변경된 사용자에게 알림을 보내는 Listener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async(value = "eventTaskExecutor")
    public void on(RoleUpdatedEvent event) {
        UUID userId = event.getUserId();
        Role oldRole = event.getOldRole();
        Role newRole = event.getNewRole();

        String title = "권한이 변경되었습니다.";
        String content = String.format("%s -> %s", oldRole, newRole);

        notificationService.create(Set.of(userId), title, content);
    }

    // S3에 파일 업로드 실패 시 알림을 보내는 Listener
    @EventListener
    @Async(value = "eventTaskExecutor")
    public void on(S3UploadFailedEvent event) {
        String requestId = event.getRequestId();
        UUID binaryContentId = event.getBinaryContentId();
        String errorMessage = event.getError().getMessage();

        String username = adminProperties.getUsername();

        // 실패 정보를 관리자에게 전송
        Set<UUID> receiverIds = userRepository.findByUsername(username)
                .map(user -> Set.of(user.getId()))
                .orElse(Set.of());

        String title = "S3 파일 업로드 실패";

        String content = String.format("""
                RequestId: %s
                BinaryContentId: %s
                Error: %s
                """,
                requestId,
                binaryContentId,
                errorMessage
        );

        notificationService.create(receiverIds, title, content);
    }
}
