package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "Notification API")
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> findAllNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    List<NotificationDto> notificationDtos = notificationService.getNotifications(
        userDetails.getUserDto().id());
    return ResponseEntity.ok(notificationDtos);
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteNotification(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @PathVariable UUID notificationId) {
    notificationService.delete(userDetails.getUserDto().id(), notificationId);
    return ResponseEntity.noContent().build();
  }
}
