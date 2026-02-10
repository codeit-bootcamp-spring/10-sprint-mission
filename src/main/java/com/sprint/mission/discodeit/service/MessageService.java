package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.MessageServiceDTO.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    List<MessageResponse> findAllByChannelId(UUID channelId) throws IOException;

    MessageResponse create(MessageCreateRequest model) throws IOException;

    MessageResponse update(MessageUpdateRequest model) throws IOException, ClassNotFoundException;

    void delete(UUID id) throws IOException, ClassNotFoundException;
}
