package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageService {
    Message sendMessage(UUID userId, UUID channelId, String content);

    List<Message> getAllMessages();

    List<Message> getMessageListByUser(UUID userId);

    List<Message> getMessageListByChannel(UUID channelId);

    Message getMessageByMessageId(UUID messageId);

    Message updateMessage(UUID messageId, String newContent);

    void deleteMessage(UUID messageId);

    void clearChannelMessage(UUID channelId);
}
