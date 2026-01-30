package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageCreateRequestDto;
import com.sprint.mission.discodeit.dto.message.MessageResponseDto;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateRequestDto messageCreateRequestDto);
    MessageResponseDto find(UUID messageId);
    List<MessageResponseDto> findallByChannelId(UUID channelId);
    MessageResponseDto update(MessageUpdateRequestDto messageUpdateRequestDto);
    void delete(UUID messageId);
}
