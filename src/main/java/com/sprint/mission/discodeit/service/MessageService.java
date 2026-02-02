package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageRequestCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageRequestUpdateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageRequestCreateDto messageRequestCreateDto);
    Message find(UUID id);
    List<MessageResponseDto> findByChannelId(UUID id);
    MessageResponseDto update(MessageRequestUpdateDto messageRequestUpdateDto);
    void deleteMessage(UUID id);
    List<MessageResponseDto> readMessagesByUser(UUID userId);
}
