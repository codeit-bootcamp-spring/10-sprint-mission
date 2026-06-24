package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.MessageService;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

// 첨부파일이 없는 단순 텍스트 메시지인 경우, STOMP를 통해 메시지를 전송할 수 있도록 하는 컨트롤러
@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

  private final MessageService messageService;

  // 첨부파일이 없는 단순 텍스트 메시지 발행
  @MessageMapping("/messages")
  public void create(
      @Payload MessageCreateRequest request,
      Principal principal
  ) {
    UUID authorId = extractUserId(principal);

    log.info(
        "Received STOMP /pub/messages request - channelId: {}, authorId: {}",
        request.channelId(),
        authorId
    );

    messageService.create(
        request.content(),
        authorId, // request.authorId()는 조작할 수 있기 때문에 사용하지 않음
        request.channelId(),
        List.of()
    );
  }

  private UUID extractUserId(Principal principal) {
    if (principal instanceof Authentication authentication
        && authentication.getPrincipal() instanceof DiscodeitUserDetails userDetails) {
      return userDetails.getId();
    }

    throw new AuthenticationCredentialsNotFoundException("인증된 WebSocket 사용자 정보를 찾을 수 없습니다.");
  }
}
