package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateDto dto);
    MessageResponseDto findMessage(UUID messageId);
    List<MessageResponseDto> findMessageByKeyword(UUID channelId, String keyword);
    List<MessageResponseDto> findAllMessagesByChannelId(UUID channelId);
    MessageResponseDto update(UUID messageId, MessageUpdateDto dto);
    void delete(UUID messageId);
}
