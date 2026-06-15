package com.sprint.mission.discodeit.notification.presentation.controller;

import com.sprint.mission.discodeit.notification.application.service.NotificationService;
import com.sprint.mission.discodeit.notification.presentation.dto.NotificationDto;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
class NotificationController {

  private final NotificationService service;

  @GetMapping
  public ResponseEntity<List<NotificationDto>> find(Principal principal) {
    UUID userId = UUID.fromString(principal.getName());
    return ResponseEntity.status(HttpStatus.OK)
        .body(service.find(userId));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@PathVariable UUID id) {
    service.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
