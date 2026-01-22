package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID,Message> messages = new LinkedHashMap<>();
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageRepository(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }




    public UserService getUserService() {
        return this.userService;
    }


    @Override
    public Message createMessage(User user, Channel channel, String content) {
        userService.findUser(user.getId());
        channelService.findChannel(channel.getId());

        Message message = new Message(user, channel, content);
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findMessage(UUID messageId) {
        Message message = messages.get(messageId);

        if (message == null) {
            throw new MessageNotFoundException();
        }
        return message;
    }

    @Override
    public List<Message> findAllMessage() {
        return new ArrayList<>(messages.values());
    }

    @Override
    public List<Message> findAllByUserMessage(UUID userId) {
        userService.findUser(userId);

        return messages.values().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelMessage(UUID channelId) {
        channelService.findChannel(channelId);

        return messages.values().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }


    @Override
    public void deleteMessage(UUID messageId) {
        if (messages.remove(messageId) == null) {
            throw new MessageNotFoundException();
        }
    }

    @Override
    public Message updateMessage(UUID messageId, String content) {
        Message message = findMessage(messageId);
        message.updateContent(content);

        return message;
    }
}
