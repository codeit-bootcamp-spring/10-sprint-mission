package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 알림 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @Override
  public ResponseEntity<List<NotificationDto>> getMyNotifications(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID userId = userDetails.getUserDto().id();
    log.debug("[Notification] 알림 목록 조회 요청: UserId={}", userId);
    return ResponseEntity.ok(notificationService.findAllByUserId(userId));
  }

  @Override
  public ResponseEntity<Void> deleteNotification(@AuthenticationPrincipal DiscodeitUserDetails userDetails, UUID notificationId) {
    UUID userId = userDetails.getUserDto().id();
    log.info("[Notification] 알림 삭제 요청: UserId={}, NotificationId={}", userId, notificationId);
    notificationService.delete(userId, notificationId);
    return ResponseEntity.noContent().build();
  }
}
