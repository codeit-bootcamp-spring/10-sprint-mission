package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

// 첨부 파일이 없는 순수 텍스트 메시지에 한해 STOMP로 수신 후 생성 위임
@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  // 클라이언트가 /pub/messages 로 발행
  // @MessageMapping 메서드가 값을 반환하면 Spring이 자동으로 @SendTo 또는 @SendToUser로 지정된 destination에 브로드캐스팅
  // 여기서는 브로드캐스트 책임을 WebSocketRequiredEventListener에 위임하므로 직접 반환하지 않음.
  @MessageMapping("/messages")
  public void create(MessageCreateRequest request) {
    log.debug("STOMP 메시지 수신: channelId={}, authorId={}",
        request.channelId(), request.authorId());
    messageService.create(request, List.of()); // 첨부파일 없는 메시지
  }
}
