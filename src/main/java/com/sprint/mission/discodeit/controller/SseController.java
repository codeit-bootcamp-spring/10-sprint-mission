package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

/**
 * SSE API를 제공 Controller
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "SSE", description = "SSE API")
public class SseController {

    private final SseService sseService;

    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails,
            @RequestParam(required = false) UUID lastEventId
    ) {
        UUID receiverId = discodeitUserDetails.getUserDto().id();
        return sseService.connect(receiverId, lastEventId);
    }
}
