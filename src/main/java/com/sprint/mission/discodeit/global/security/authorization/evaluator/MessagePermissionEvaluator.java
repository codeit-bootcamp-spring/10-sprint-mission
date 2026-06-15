package com.sprint.mission.discodeit.global.security.authorization.evaluator;

import com.sprint.mission.discodeit.message.domain.entity.Message;
import com.sprint.mission.discodeit.message.infra.repository.MessageRepository;

public class MessagePermissionEvaluator extends
    AbstractPermissionEvaluator<Message> {

  public MessagePermissionEvaluator(MessageRepository repository) {
    super("Message", repository);
  }
}
