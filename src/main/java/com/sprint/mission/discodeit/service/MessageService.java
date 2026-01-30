package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageDTO;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageDTO.Response create(MessageDTO.Create createRequest);
    MessageDTO.Response findById(UUID messageId);
    List<MessageDTO.Response> findAllByChannelId(UUID channelId);
    MessageDTO.Response update(UUID authorId, MessageDTO.Update updateRequest);
    void delete(UUID authorId, UUID messageId);
}
