package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notificationdto.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<List<NotificationDto>> getNotifications() {
    return ResponseEntity.ok(notificationService.findAllByCurrentUser());
  }

  @DeleteMapping("/{notificationId}")
  public ResponseEntity<Void> checkNotification(@PathVariable UUID notificationId) {
    notificationService.delete(notificationId);
    return ResponseEntity.noContent().build();
  }
}
