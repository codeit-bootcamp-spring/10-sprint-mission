package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final HashMap<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }

    @Override
    public Message create(String msg, UUID userId, UUID channelId) {
        Message message = new Message(msg, userId, channelId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        Message message = Optional.ofNullable(data.get(id)).orElseThrow(() -> new NoSuchElementException());
        return message;
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Message update(UUID id, String messageData) throws NoSuchElementException{
        this.read(id).updateText(messageData);
        return this.read(id);
    }

    @Override
    public void delete(UUID id) throws NoSuchElementException{
        this.read(id);
        this.data.remove(id);
    }

    @Override
    public List<Message> readUserMessageList(User user) {
        return user.getMessageList();
    }

    @Override
    public List<Message> readChannelMessageList(Channel channel) {
        return channel.getMessagesList();
    }
}
