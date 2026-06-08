package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import com.sprint.mission.discodeit.service.auth.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "4. 알림 관리", description = "알림 조회 및 삭제 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @Operation(summary = "알림 목록 조회", description = "특정 사용자에게 온 알림 목록을 조회합니다.")
  @GetMapping
  public List<NotificationDto> findAllByReceiverId(
      Authentication authentication
  ) {
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
    UUID receiverId = principal.getUserDto().getId();

    log.info("[REQUEST] Get Notifications: receiverId={}", receiverId);

    return notificationService.findAllByReceiverId(receiverId);
  }

  @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
  @DeleteMapping("/{notificationId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(
      @PathVariable UUID notificationId,
      Authentication authentication
  ) {
    DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();
    UUID requesterId = principal.getUserDto().getId();

    log.info("[REQUEST] Delete Notification: id={}, requesterId={}",
        notificationId, requesterId);

    notificationService.delete(notificationId, requesterId);
  }
}
