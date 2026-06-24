package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails,
            @RequestParam(value = "lastEventId", required = false, defaultValue = "") String lastEventId
    ) {
        UUID userId = userDetails.getUserDto().id();
        UUID lastEventIdUuid = null;
        if (!lastEventId.isEmpty()) {
            try {
                lastEventIdUuid = UUID.fromString(lastEventId);
            } catch (IllegalArgumentException e) {
                // log 처리 필요
            }
        }
        
        SseEmitter sseEmitter = sseService.connect(userId, lastEventIdUuid);
        return ResponseEntity.ok(sseEmitter);
    }
}
