package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(UUID channelId, UUID authorId, MessageCreateRequest messageCreateRequest);
    MessageDto find(UUID messageId);
    List<MessageDto> findAllByChannelId(UUID channelId, UUID userId);
    MessageDto update(UUID channelId, UUID authorId, UUID messageId, MessageUpdateRequest messageUpdateRequest);
    void delete(UUID channelId, UUID authorId, UUID messageId);
}
