package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message create(Message message);
    Message findById(UUID id);
    List<Message> findAll();
    Message update(UUID id, String content, UUID channelId, UUID userId);

    void delete(UUID id);
}
