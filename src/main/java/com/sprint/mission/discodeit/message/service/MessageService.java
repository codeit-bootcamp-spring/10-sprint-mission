package com.sprint.mission.discodeit.message.service;

import com.sprint.mission.discodeit.message.dto.MessageCreateRequest;
import com.sprint.mission.discodeit.message.dto.MessageResponse;
import com.sprint.mission.discodeit.message.dto.MessageUpdateRequest;
import com.sprint.mission.discodeit.message.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    MessageResponse create(MessageCreateRequest request);
    Message find(UUID messageId);
    Optional<Message> findByChannelId(UUID channelId);
    List<MessageResponse> findAllByChannelId(UUID channelId);
    MessageResponse update(MessageUpdateRequest request);
    void delete(UUID messageId);
}
