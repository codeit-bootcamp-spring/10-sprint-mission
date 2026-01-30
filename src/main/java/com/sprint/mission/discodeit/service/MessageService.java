package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.*;
import com.sprint.mission.discodeit.entity.Message;

public interface MessageService extends DomainService<Message, MessageCreation, MessageContentUpdate> {
}
