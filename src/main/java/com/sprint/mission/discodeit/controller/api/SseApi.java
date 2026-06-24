package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "SSE", description = "Server-Sent Events API")
public interface SseApi {

  @Operation(summary = "SSE 연결", description = "실시간 이벤트 수신을 위한 SSE 스트림에 연결한다.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "SSE 연결 성공 (text/event-stream)"),
      @ApiResponse(responseCode = "401", description = "인증 필요")
  })
  SseEmitter connect(
      @Parameter(hidden = true) @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @Parameter(description = "마지막으로 수신한 이벤트 ID (유실 복원용)")
      @RequestParam(value = "lastEventId", required = false) String lastEventId,
      @Parameter(hidden = true)
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader
  );
}
