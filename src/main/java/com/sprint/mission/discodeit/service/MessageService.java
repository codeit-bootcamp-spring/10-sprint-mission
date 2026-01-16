package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {

    Message create(UUID channelId, UUID senderId, String text);
    Message read(UUID id);
    List<Message> readAll();
    List<Message> getMessagesByUser(UUID userId);
    List<Message> getMessagesByChannel(UUID channelId);
    Message update(UUID id, String text);
    void delete(UUID id);
    void deleteMessageByUserId(UUID userId);
    void deleteMessageByChannelId(UUID channelId);

}
