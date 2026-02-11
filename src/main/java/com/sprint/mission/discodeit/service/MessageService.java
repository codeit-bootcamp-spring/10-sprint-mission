package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateMessageRequestDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UpdateMessageRequestDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto create(CreateMessageRequestDto request);
    MessageDto find(UUID messageId);
    List<MessageDto> findAllByChannelId(UUID channelId);
    MessageDto update(UUID messageId, UpdateMessageRequestDto request);
    void delete(UUID messageId);
}
