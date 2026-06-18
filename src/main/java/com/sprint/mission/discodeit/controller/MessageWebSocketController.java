package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public void sendMessage(MessageCreateRequest messageCreateRequest) {
    messageService.create(messageCreateRequest, Collections.emptyList());
  }

}
