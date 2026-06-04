package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.entity.Notification;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
@Tag(name = "Notification")
@Slf4j
public class NotificationController {

  private final NotificationService notificationService;

  @GetMapping
  @Operation(summary = "Notification 목록 조회")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "알림 목록이 정상적으로 조회됨"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
  })
  public ResponseEntity<List<NotificationDto>> findAll(@AuthenticationPrincipal
  DiscodeitUserDetails userDetails) {
    List<NotificationDto> responses = notificationService.findAll(userDetails.getUserDto().id());
    return ResponseEntity.status(HttpStatus.OK).body(responses);
  }

  @PreAuthorize("@basicNotificationService.isNotificationReceiver(#notificationId, authentication.principal.userDto.id)")
  @DeleteMapping("/{notificationId}")
  @Operation(summary = "Notification 확인")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "알림이 정상적으로 확인됨"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
      @ApiResponse(responseCode = "403", description = "인가되지 않은 요청")
  })
  public ResponseEntity<Void> delete(@PathVariable UUID notificationId) {
    notificationService.delete(notificationId);
    return ResponseEntity.noContent().build();
  }
}
