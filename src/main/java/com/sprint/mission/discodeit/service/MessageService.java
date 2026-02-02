package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(UUID requestId, UUID channelId, String content);

    Message findMessageByMessageId(UUID id);

    List<Message> findAllMessages();

    Message updateMessage(UUID requestId, UUID messageId, String content);

    void deleteMessage(UUID requestId, UUID messageId);
}
