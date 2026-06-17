package com.sprint.mission.discodeit.notification.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.notification.dto.NotificationDto;
import com.sprint.mission.discodeit.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Notification")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> findNotification(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    List<NotificationDto> result = notificationService.findNotificationsByReceiverId(
        userDetails.getUserDto()
            .id());
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  ) {
    notificationService.confirmNotification(notificationId, userDetails.getUserDto().id());
    return ResponseEntity.noContent().build();
  }
}
