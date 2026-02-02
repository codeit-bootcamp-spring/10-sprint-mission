package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.message.MessageRequestDto;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(MessageRequestDto messageCreateDto);
    List<Message> findallByChannelId(UUID ChannelId);
    Message update(UUID messageId, MessageRequestDto messageUpdateDto);
    void delete(UUID messageId);
}
