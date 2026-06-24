package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

// STOMP로 들어온 텍스트 메시지 생성 요청을 기존 MessageService 생성 로직으로 위임하는 Controller
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final MessageService messageService;

    // /pub/message로 들어온 첨부파일이 없는 메시지를 생성
    @MessageMapping("/messages")
    public void sendMessage(@Valid MessageCreateRequest request) {
        messageService.create(request, null);
    }
}
