package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDto;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDto.Response create(MessageDto.Create createRequest);
    MessageDto.Response findById(UUID messageId);
    List<MessageDto.Response> findAllByChannelId(UUID channelId);
    MessageDto.Response update(UUID authorId, MessageDto.Update updateRequest);
    void delete(UUID authorId, UUID messageId);
}
