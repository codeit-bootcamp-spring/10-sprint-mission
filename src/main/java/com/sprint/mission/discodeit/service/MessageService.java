package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto create(MessageCreateDto dto);
    MessageResponseDto find(UUID id);
    List<MessageResponseDto> findallByChannelId(UUID channelId);
    MessageResponseDto update(MessageUpdateDto dto);
    void delete(UUID id);
}
