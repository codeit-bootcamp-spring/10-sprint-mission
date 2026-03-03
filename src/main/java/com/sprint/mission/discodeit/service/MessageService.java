package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.*;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    List<MessageResponse> findAllByChannelId(UUID channelId);

    MessageResponse create(MessageCreateCommand command);

    MessageResponse update(MessageUpdateCommand command);

    void delete(UUID id);
}
