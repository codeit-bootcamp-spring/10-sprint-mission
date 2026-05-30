package com.sprint.mission.discodeit.evaluator;

import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePermissionEvaluator {

  private final MessageRepository messageRepository;

  // 메시지의 id와 작성자의 id를 비교하여 작성자 확인하는 메서드
  public boolean isAuthor(UUID messageId, UUID authorId) {
    return messageRepository.findById(messageId)
        .map(message -> message.getAuthor().getId().equals(authorId))
        .orElse(false);
  }
}
