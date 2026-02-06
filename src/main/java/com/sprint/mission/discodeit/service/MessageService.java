package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(UUID channelId, UUID authorId, MessageCreateDto messageCreateDto);
    MessageResponseDto find(UUID messageId);
    List<MessageResponseDto> findAllByChannelId(UUID userId, UUID channelId);
    MessageResponseDto update(UUID id, UUID userId, MessageUpdateDto messageUpdateDto);
    void delete(UUID messageId, UUID userId);
}
