package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID,Message> messageStore;

    public JCFMessageService(){
        this.messageStore = new HashMap<>();
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        Message newMessage = new Message(userId, channelId, content);
        messageStore.put(newMessage.getId(), newMessage);
        return newMessage;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return messageStore.get(messageId);
    }

    public List<Message> findMessageByUserId(UUID userId){
        return messageStore.values().stream()
                .filter(message -> message.getUserId().equals(userId))
                .toList();
    }

    public List<Message> findMessageByChannelId(UUID channelId) {
        return messageStore.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    public void deleteMessage(UUID messageId){
            messageStore.remove(messageId);
    }
}
