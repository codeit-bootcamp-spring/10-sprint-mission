package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageRequestDto messageCreateDto);
    List<MessageResponseDto> findallByChannelId(UUID ChannelId);
    MessageResponseDto update(UUID messageId, MessageRequestDto messageUpdateDto);
    void delete(UUID messageId);
}
