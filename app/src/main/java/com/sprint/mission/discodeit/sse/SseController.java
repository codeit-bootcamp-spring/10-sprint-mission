package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/sse")
public class SseController {

  private final SseService sseService;

  @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public SseEmitter getSseEmitter(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails,
      @RequestParam(required = false) UUID lastEventId)
      throws IOException {
    UUID receiverId = userDetails.getUserDto().id();
    return sseService.connect(receiverId, lastEventId);
  }
}
