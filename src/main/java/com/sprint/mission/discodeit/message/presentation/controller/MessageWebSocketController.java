package com.sprint.mission.discodeit.message.presentation.controller;

import com.sprint.mission.discodeit.global.web.ws.stomp.constant.WebSocketDestinations;
import com.sprint.mission.discodeit.message.application.service.MessageService;
import com.sprint.mission.discodeit.message.presentation.dto.request.MessageCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
class MessageWebSocketController {

  private final MessageService service;

  @MessageMapping(WebSocketDestinations.PUB_MESSAGE)
  public void send(@Payload MessageCreateRequest request) {
    service.create(request);
  }
}
