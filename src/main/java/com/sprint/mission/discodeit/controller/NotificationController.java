package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.NotificationApi;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

  private final NotificationService notificationService;

  @GetMapping
  @Override
  public ResponseEntity<List<NotificationDto>> findAll(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID receiverId = userDetails.getUserDto().id();
    List<NotificationDto> notifications = notificationService.findAllByReceiverId(receiverId);
    log.info("알림 목록 조회 완료: receiverId={}, count={}", receiverId, notifications.size());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(notifications);
  }

  @DeleteMapping("{notificationId}")
  @Override
  public ResponseEntity<Void> delete(@PathVariable("notificationId") UUID notificationId) {
    notificationService.delete(notificationId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .build();
  }
}
