package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.basic.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SseController {
    private final SseService sseService;

    // 사용자A의 id: receiverId = 111
    /**
    [클라이언트 요청] : 서버와 SSE 연결 생성 요청
    GET /api/sse?receiverId=111
    Accept: text/event-stream

    **/
    // SseEmitter 객체를 저장해두고, 나중에 서버 이벤트가 발생하면 그 객체로 데이터를 밀어 넣는다.
    @GetMapping(value = "/api/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails,
            @RequestHeader(value = "Last-Event-ID", required = false) UUID lastEventId ) {
        return sseService.connect(userDetails.getUserDto().id(), lastEventId);
    }
}
