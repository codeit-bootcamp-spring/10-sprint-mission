package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.mapper.NotificationMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;
  private final NotificationMapper notificationMapper;


  @Override
  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    log.info("Received GET /api/notifications request - userId: {}", userDetails.getId());

    List<Notification> notifications = notificationService.findAllByReceiverId(userDetails.getId());

    List<NotificationDto> response = notifications.stream()
        .map(notificationMapper::toDto)
        .toList();

    return ResponseEntity.ok(response); // 200 OK
  }

  // 알림 확인 및 삭제
  @Override
  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {

    log.info("Received DELETE /api/notifications/{} request", notificationId);
    notificationService.deleteById(notificationId, userDetails.getId());
    return ResponseEntity.noContent().build(); // 204 No Content
  }
}
