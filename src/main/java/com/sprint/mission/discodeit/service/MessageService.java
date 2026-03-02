package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message createMessage(UUID requestId, MessageCreateRequest request,
                          List<BinaryContentRequest> attachments);

    List<Message> findAllMessagesByChannelId(UUID channelId);

    Message updateMessage(UUID messageId, MessageUpdateRequest request);

    void deleteMessage(UUID messageId);
}
