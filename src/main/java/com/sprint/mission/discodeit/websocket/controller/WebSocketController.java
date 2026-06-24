package com.sprint.mission.discodeit.websocket.controller;

import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(MessageCreateRequest request) {
    messageService.create(request, java.util.List.of());
  }

}
