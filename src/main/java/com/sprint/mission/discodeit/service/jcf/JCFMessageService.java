package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final List<Message> messages = new ArrayList<>();
    private final JCFChannelService channelService;

    public JCFMessageService(JCFChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID senderId, String content) {
        Channel channel = channelService.findChannel(channelId);
        User sender = channelService.getUserService().findUser(senderId);
        Message message = new Message(sender, channel, content);
        messages.add(message);
        return message;
    }

    @Override
    public Message findMessage(UUID channelId, UUID messageId) {
        Channel channel = channelService.findChannel(channelId);
        return messages.stream()
                .filter(m -> m.getId().equals(messageId) && m.getChannel().equals(channel))
                .findFirst()
                .orElseThrow(() -> new MessageNotFoundException("해당 메시지가 존재하지 않습니다."));
    }

    @Override
    public List<Message> findAllByChannelMessage(Channel channel) {
        List<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (message.getChannel().equals(channel)) result.add(message);
        }
        return result;
    }

    @Override
    public List<Message> findAllMessage(UUID channelId) {
        return new ArrayList<>(messages);
    }

    @Override
    public Message updateMessage(UUID channelId, UUID messageId, String newContent) {
        Message message = findMessage(channelId, messageId);
        message.updateContent(newContent);
        return message;
    }

    @Override
    public void deleteMessage(UUID channelId, UUID messageId) {
        Message message = findMessage(channelId, messageId);
        messages.remove(message);
    }
}
