package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.service.auth.DiscodeitUserDetails;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

  private final SseService sseService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter connect(
      Authentication authentication,
      @RequestHeader(value = "Last-Event-ID", required = false)
      UUID lastEventIdHeader,
      @RequestParam(value = "lastEventId", required = false)
      UUID lastEventIdParam
  ) {
    DiscodeitUserDetails userDetails =
        (DiscodeitUserDetails) authentication.getPrincipal();

    UUID userId = userDetails.getUserDto().getId();

    UUID lastEventId =
        lastEventIdHeader != null
            ? lastEventIdHeader
            : lastEventIdParam;

    return sseService.connect(userId, lastEventId);
  }
}
