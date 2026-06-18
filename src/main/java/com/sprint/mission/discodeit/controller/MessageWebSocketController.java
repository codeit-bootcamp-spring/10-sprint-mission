package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageWebSocketController {

    private final MessageService messageService;

    @MessageMapping("/messages")
    public void create(@Valid MessageCreateRequest messageCreateRequest) {
        log.info("STOMP 메시지 생성 요청: {}", messageCreateRequest);
        MessageDto createdMessage = messageService.create(messageCreateRequest, Collections.emptyList());
        log.debug("STOMP 메시지 생성 응답: {}", createdMessage);
    }
}
