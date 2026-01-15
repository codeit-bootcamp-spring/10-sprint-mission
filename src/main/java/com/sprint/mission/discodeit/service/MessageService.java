package com.sprint.mission.discodeit.service;
import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(String message, UUID userId, UUID channelId);

    Message findById(UUID messageId);

    List<Message> findAllByChannelId(UUID channelId);

    Message update(UUID id, String content);

    void delete(UUID id);

    List<Message> getMessageList(UUID channelId);
}
