package com.sprint.mission.discodeit.global.security.authorization.evaluator;

import com.sprint.mission.discodeit.message.entity.Message;
import com.sprint.mission.discodeit.message.repository.MessageRepository;

public class MessagePermissionEvaluator extends
    AbstractPermissionEvaluator<Message> {

  public MessagePermissionEvaluator(MessageRepository repository) {
    super("Message", repository);
  }
}
