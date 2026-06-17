package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications(
      Authentication authentication
  ) {
    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) authentication.getPrincipal();

    return ResponseEntity.ok(notificationService.findAllNotification(userDetails.getId()));
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> deleteAllNotifications(
      Authentication authentication,
      @PathVariable UUID notificationId
  ) {
    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) authentication.getPrincipal();

    notificationService.deleteNotification(notificationId, userDetails.getId());

    return ResponseEntity.noContent().build();
  }

}
