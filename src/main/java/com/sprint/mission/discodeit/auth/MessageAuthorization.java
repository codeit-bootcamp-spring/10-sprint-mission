package com.sprint.mission.discodeit.auth;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 메시지에 대한 동적 권한 검증을 담당하는 컴포넌트입니다.
 * SpEL(Spring Expression Language)을 통해 호출됩니다.
 */
@Component("messageAuth")
@RequiredArgsConstructor
public class MessageAuthorization {
  private final MessageRepository messageRepository;

  /**
   * 요청자가 메시지의 작성자인지 확인합니다.
   */
  public boolean isAuthor(UUID messageId, UUID userId) {
    return messageRepository.existsByIdAndAuthor_Id(messageId, userId);
  }
}
