package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.exception.notification.NotificationAccessDeniedException;
import com.sprint.mission.discodeit.exception.notification.NotificationNotFoundException;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.repository.NotificationRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicNotificationService implements NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;

  // 알림 목록 조회
  @Override
  public List<Notification> findAllByReceiverId(UUID receiverId) {
    log.debug("알림 목록 조회 요청: 사용자 ID = {}", receiverId);

    return notificationRepository.findAllByReceiverIdOrderByCreatedAtDesc(receiverId);
  }

  // 알림 확인 및 삭제
  @Override
  @Transactional
  public void deleteById(UUID notificationId, UUID requesterId) {
    log.info("알림 삭제 요청: 알림 ID = {}, 요청자 ID = {}", notificationId, requesterId);

    // 알림 조회
    Notification notification = notificationRepository.findById(notificationId)
        // 해당 알림을 찾을 수 없을 때 404 예외
        .orElseThrow(() -> new NotificationNotFoundException(
            Map.of("requestedNotificationId", notificationId))); // 404 예외

    // 알림 수신자와 알림 삭제 요청자가 같은지 검증
    if (!notification.getReceiver().getId().equals(requesterId)) {
      log.warn("권한 없는 알림 삭제 시도: 요청자 ID = {}", requesterId);
      // 알림 수신자와 알림 삭제 요청자가 다를 때 403 예외
      throw new NotificationAccessDeniedException(Map.of(
          "notificationId", notificationId,
          "requesterId", requesterId
      ));
    }

    notificationRepository.delete(notification);
    log.info("알림 삭제 완료: ID = {}", notificationId);
  }
}