package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final List<Message> data = new ArrayList<>();

    @Override
    public Message createMessage(String content, User sender, Channel channel) {
        Message message = new Message(content, sender, channel);
        data.add(message);
        channel.addMessage(message);
        return message;
    }

    @Override
    public Message getMessage(UUID messageId) {
        return findById(messageId);
    }

    @Override
    public List<Message> getAllMessages() {
        return List.copyOf(data);
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message findMessage = findById(messageId);
        Optional.ofNullable(content)
                .ifPresent(findMessage::updateContent);
        return findMessage;
    }

    @Override
    public Message deleteMessage(UUID messageId) {
        Message target = findById(messageId);
        target.getChannel().removeMessage(target);
        data.remove(target);
        return target;
    }

    private Message findById(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 존재하지 않습니다."));
    }
}
