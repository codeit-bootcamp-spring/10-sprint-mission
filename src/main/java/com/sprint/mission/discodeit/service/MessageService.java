package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message create(MessageCreateRequest request);
    Message find(UUID messageId);
    Optional<Message> findByChannelId(UUID channelId);
    List<Message> findallByChannelId(UUID channelId);
    void update(MessageUpdateRequest request);
    void delete(UUID messageId);
}
