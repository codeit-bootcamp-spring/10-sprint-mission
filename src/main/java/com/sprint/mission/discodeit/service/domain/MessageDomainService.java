package com.sprint.mission.discodeit.service.domain;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class MessageDomainService {
    private final MessageService messageService;
    private final UserService userService;
    private final ChannelService channelService;

    public MessageDomainService(MessageService messageService, UserService userService, ChannelService channelService) {
        this.messageService = messageService;
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message createMessage(UUID requestId, UUID channelId, String content) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);
        Message message = requester.sendMessage(channel, content);

        messageService.save(message);
        return message;
    }

    public Message findMessageByMessageId(UUID id) {
        return messageService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지를 찾을 수 없습니다 messageId: " + id));
    }

    public List<Message> findAllMessages() {
        return messageService.findAll();
    }

    public Message updateMessage(UUID requestId, UUID messageId, String content) {
        User requester = findUserByUserID(requestId);
        Message message = findMessageByMessageId(messageId);

        message.validateOwner(requester);
        message.updateContent(content);

        return message;
    }

    public void deleteMessage(UUID requestId, UUID messageId) {
        User requester = findUserByUserID(requestId);
        Message message = findMessageByMessageId(messageId);

        message.validateOwner(requester);
        message.clear();

        messageService.delete(message);
    }

    private User findUserByUserID(UUID userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel findChannelByChannelId(UUID id) {
        return channelService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }
}
