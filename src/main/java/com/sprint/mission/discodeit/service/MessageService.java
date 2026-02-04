package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    UUID createMessage(UUID requestId, CreateMessageRequest request);

    List<MessageResponse> findAllMessagesByChannelId(UUID channelId);

    MessageResponse updateMessage(UUID requestId, UpdateMessageRequest request);

    void deleteMessage(UUID requestId, UUID messageId);
}
