package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(@RequestParam("receiverId") UUID receiverId,
                                              @RequestParam(value = "lastEventId", required = false) UUID lastEventId){
        SseEmitter response = sseService.connect(receiverId, lastEventId);

        return ResponseEntity.ok(response);
    }
}
