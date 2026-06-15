package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {
    private final BasicMessageService messageService;

    /// @Payload: STOMP 메시지 바디를 이 파라미터에 매핑해줘.
    @MessageMapping("/messages") //클라이언트의 /pub/messages 요청 처리
    public void sendMessage(@Valid @Payload MessageCreateRequest request) {
        messageService.create(request, List.of());
    }
}
