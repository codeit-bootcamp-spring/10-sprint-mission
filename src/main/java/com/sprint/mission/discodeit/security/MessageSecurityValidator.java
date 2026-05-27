package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("messageSecurityValidator")
@RequiredArgsConstructor
public class MessageSecurityValidator {

  private final MessageRepository messageRepository;

  // DB에서 메시지를 찾고, 그 메시지의 작성자 ID와 현재 로그인한 유저 ID가 같은지 확인
  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(userId))
        .orElse(false); // 메시지가 없을 시 false(403) 튕김 방어
  }
}
