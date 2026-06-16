package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

@Validated
@Slf4j
@RequiredArgsConstructor
@Controller
public class MessageWebSocketController {

  private final MessageService messageService;

  @MessageMapping("/messages")
  public MessageDto create(@Valid MessageCreateRequest request) {
    MessageDto createdMessage = messageService.create(request, List.of());

    log.debug("웹소켓 메세지 생성 완료: messageId={}", createdMessage);
    return createdMessage;
  }
}
