package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {

    @Override
    public Message createMessage(UUID channelId, UUID userId, String msg) {
        return null;
    }

    @Override
    public List<String> readMessagesByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public List<String> readMessagesByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID channelId) {
        return List.of();
    }

    @Override
    public Message findMessageByChannelIdAndMessageId(UUID channelId, UUID messageId) {
        return null;
    }

    @Override
    public Message updateMessageContent(UUID channelId, UUID userId, UUID messageId, String newMessage) {
        return null;
    }

    @Override
    public void deleteMessage(UUID channelId, UUID userId, UUID messageId) {

    }
}
