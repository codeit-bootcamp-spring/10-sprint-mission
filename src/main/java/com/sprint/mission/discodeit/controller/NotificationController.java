package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "notification")
@RequestMapping("/api/notifications")
public class NotificationController {

  private final NotificationService notificationService;


  @Operation(summary = "Notification 요청")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Notifications가 성공적으로 반환됨"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping
  public ResponseEntity<List<NotificationDto>> getNotifications(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(notificationService.getNotifications(userDetails.getUserDto().id()));
  }

  @Operation(summary = "Notification 삭제")
  @ApiResponses({
      @ApiResponse(responseCode = "204", description = "Notification가 성공적으로 삭제됨"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "403", description = "인가되지 않은 요청",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "알림이 없는 경우",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @DeleteMapping("/{notification-id}")
  public ResponseEntity<Void> deleteNotification(
      @PathVariable("notification-id") UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    UUID userId = userDetails.getUserDto().id();
    notificationService.deleteNotification(notificationId, userId);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
