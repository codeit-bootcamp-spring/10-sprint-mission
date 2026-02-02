package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.MessageResponse;
import com.sprint.mission.discodeit.dto.MessageUpdateRequest;

import java.util.*;

public interface MessageService {
    MessageResponse createMessage(MessageCreateRequest request);

    MessageResponse getMessage(UUID id);

    List<MessageResponse> getAllMessages();

    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse updateMessage(MessageUpdateRequest request);

    void deleteMessage(UUID id);

    List<MessageResponse> getMessagesByUserId(UUID userId);
}