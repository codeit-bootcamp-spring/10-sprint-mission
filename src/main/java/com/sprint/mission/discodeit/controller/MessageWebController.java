package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MessageWebController {


    private final MessageService messageService;

    /**
     * 클라이언트가 /pub/messages 로 메시지를 보낼 때 호출됩니다.
     * 메시지를 저장하면 MessageWebSocketListener에 의해 실시간으로 브로드캐스트됩니다.
     */
    @MessageMapping("/messages")
    public void sendMessage(@Payload MessageDto.CreateRequest request) {
        messageService.create(request, List.of());
    }
}
