package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationApi {

  @Operation(summary = "알림 목록 조회", description = "현재 로그인한 사용자의 모든 알림을 최신순으로 조회합니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "알림 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
      ),
      @ApiResponse(
          responseCode = "401",
          description = "인증되지 않은 요청 (토큰 없음 또는 만료)",
          content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"A001\", \"message\": \"로그인이 필요한 서비스입니다.\"}"))
      )
  })
  ResponseEntity<List<NotificationDto>> getNotifications(
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "알림 확인 및 삭제", description = "특정 알림을 읽음 처리(삭제)합니다. 본인의 알림만 삭제할 수 있습니다.")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204",
          description = "알림 삭제 성공"
      ),
      @ApiResponse(
          responseCode = "401",
          description = "인증되지 않은 요청"
      ),
      @ApiResponse(
          responseCode = "403",
          description = "인가되지 않은 요청 (본인의 알림이 아님)",
          content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"G003\", \"message\": \"접근 권한이 없습니다.\"}"))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "알림을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "{\"errorCode\": \"N001\", \"message\": \"해당 알림을 찾을 수 없습니다.\"}"))
      )
  })
  ResponseEntity<Void> deleteNotification(
      @Parameter(description = "삭제할 알림 ID") @PathVariable UUID notificationId,
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

}
