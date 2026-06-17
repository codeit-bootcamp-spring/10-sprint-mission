package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
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
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Notification", description = "알림 API")
public interface NotificationApi {

  @Operation(summary = "내 알림 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "알림 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
      )
  })
  ResponseEntity<List<NotificationDto>> findAll(
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "알림 삭제")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "알림이 성공적으로 삭제됨"),
      @ApiResponse(
          responseCode = "404", description = "알림을 찾을 수 없음",
          content = @Content(examples = @ExampleObject(value = "Notification with id {notificationId} not found"))
      ),
      @ApiResponse(
          responseCode = "403", description = "본인 알림만 삭제할 수 있음",
          content = @Content(examples = @ExampleObject(value = "Forbidden"))
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 알림 ID") UUID notificationId,
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );
}
