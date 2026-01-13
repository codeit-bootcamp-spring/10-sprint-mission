package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private static JCFMessageService instance = null;
    private JCFMessageService(){}
    public static JCFMessageService getInstance(){
        if(instance == null){
            instance = new JCFMessageService();
        }
        return instance;
    }

    HashSet<Message> messages = new HashSet<>();

    @Override
    public Message find(UUID id) {
        return messages.stream()
                .filter(message -> id.equals(message.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message not found: id = " + id));
    }

    @Override
    public HashSet<Message> findAll() {
        HashSet<Message> newMessages = new HashSet<>();
        newMessages.addAll(messages);
        return newMessages;
    }

    @Override
    public Message create(UUID userID, String msg, UUID channelID) {
        User user = JCFUserService.getInstance().find(userID);
        Channel channel = JCFChannelService.getInstance().find(channelID);

        Message newMessage = new Message(user, msg, channel);
        messages.add(newMessage);
        return newMessage;
    }

    @Override
    public void delete(UUID id) {
        messages.remove(find(id));
    }

    @Override
    public Message update(UUID id, String msg) {
        this.find(id)
                .updateMessage(msg);
        return this.find(id);
    }
}
