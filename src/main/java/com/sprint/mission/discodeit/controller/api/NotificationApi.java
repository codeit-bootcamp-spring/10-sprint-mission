package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@Tag(name = "Notification", description = "Notification API")
public interface NotificationApi {

  @Operation(summary = "로그인한 사용자의 알림 목록 조회")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200", description = "알림 목록 조회 성공",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class)))
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청"
      )
  })
  ResponseEntity<List<NotificationDto>> findAll(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );

  @Operation(summary = "알림 확인 (삭제)")
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "204", description = "알림이 성공적으로 삭제됨"
      ),
      @ApiResponse(
          responseCode = "401", description = "인증되지 않은 요청"
      ),
      @ApiResponse(
          responseCode = "403", description = "인가되지 않은 요청 (본인의 알림만 삭제 가능)"
      ),
      @ApiResponse(
          responseCode = "404", description = "알림을 찾을 수 없음"
      )
  })
  ResponseEntity<Void> delete(
      @Parameter(description = "삭제할 알림 ID") UUID notificationId,
      @AuthenticationPrincipal DiscodeitUserDetails userDetails
  );
}
