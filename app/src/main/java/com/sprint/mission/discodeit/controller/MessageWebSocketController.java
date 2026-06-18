package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageDto.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")    // prefix 설정해둬서 `/pub` 생략
  public void sendMessages(MessageCreateRequest request) throws IOException {
    messageService.createMessage(request, null);
  }
}
