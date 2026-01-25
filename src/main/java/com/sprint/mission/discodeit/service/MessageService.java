package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    Message createMessage(String content, UUID senderId, UUID channelId);
    Message findId(UUID massageId);
    List<Message> findAll();
    List<Message> findMessagesById(UUID userId);
    List<Message> findMessagesByChannel(UUID channelId);
    List<Message> findMessagesByUserAndChannel(UUID userId, UUID channelId);
    Message update(UUID massageId, String content);
    void delete(UUID massageId);

    void deleteAll();
}
