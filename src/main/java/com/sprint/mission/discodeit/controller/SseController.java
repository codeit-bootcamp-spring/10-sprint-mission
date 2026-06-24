package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.SseApi;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sse")
public class SseController implements SseApi {

  private final SseService sseService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @Override
  public SseEmitter connect(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @RequestParam(value = "lastEventId", required = false) String lastEventId,
      @RequestHeader(value = "Last-Event-ID", required = false) String lastEventIdHeader) {
    UUID receiverId = userDetails.getUserDto().id();
    UUID resolvedLastEventId = parseUuid(lastEventId != null ? lastEventId : lastEventIdHeader);
    return sseService.connect(receiverId, resolvedLastEventId);
  }

  private UUID parseUuid(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return UUID.fromString(value);
    } catch (IllegalArgumentException e) {
      log.debug("유효하지 않은 lastEventId 무시 (value={})", value);
      return null;
    }
  }
}
