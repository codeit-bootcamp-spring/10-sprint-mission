package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicMessageService implements MessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String content) {
        Channel channel = channelService.getChannel(channelId);
        User user = userService.getUser(userId);

        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("채널에 먼저 입장해야 메시지를 남길 수 있습니다.");
        }
        validateMessageContent(content);
        Message message = new Message(channel, user, content);

        channel.addMessage(message);
        user.addMessage(message);

        messageRepository.save(message);
        channelRepository.save(channel);
        userRepository.save(user);

        return message;
    }

    @Override
    public Message getMessage(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지입니다."));
    }

    @Override
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(String content, UUID id) {
        Message message = getMessage(id);
        Optional.ofNullable(content)
                .filter(c -> !c.isBlank())
                .ifPresent(message::updateContent);

        messageRepository.save(message);

        User msgUser = message.getUser();
        if (msgUser != null && msgUser.getId() != null) {
            User user = userRepository.findById(msgUser.getId())
                    .orElse(null);

            if (user != null) {
                user.getMessages().removeIf(m -> m != null && id.equals(m.getId()));
                user.getMessages().add(message);

                userRepository.save(user);
            }
        }

        Channel msgChannel = message.getChannel();
        if (msgChannel != null && msgChannel.getId() != null) {
            Channel channel = channelRepository.findById(msgChannel.getId())
                    .orElse(null);

            if (channel != null) {
                channel.getMessages().removeIf(m -> m != null && id.equals(m.getId()));
                channel.getMessages().add(message);

                channelRepository.save(channel);
            }
        }

        return message;
    }

    @Override
    public void deleteMessage(UUID id) {
        Message message = getMessage(id);

        if (message.getChannel() != null) {
            UUID channelId = message.getChannel().getId();
            channelRepository.findById(channelId).ifPresent(channel -> {
                // iterator 충돌 방지 및 ID 기반 삭제
                channel.getMessages().removeIf(m -> m.getId().equals(id));
                channelRepository.save(channel);
            });
        }

        if (message.getUser() != null) {
            UUID userId = message.getUser().getId();
            userRepository.findById(userId).ifPresent(user -> {
                user.getMessages().removeIf(m -> m.getId().equals(id));
                userRepository.save(user);
            });
        }

        messageRepository.delete(id);
    }

    @Override
    public List<Message> getMessagesByChannelId(UUID channelId) {
        return new ArrayList<>(channelService.getChannel(channelId).getMessages());
    }

    @Override
    public List<Message> getMessagesByUserId(UUID userId) {
        return new ArrayList<>(userService.getUser(userId).getMessages());
    }

    private void validateMessageContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("내용을 다시 입력해주세요");
        }
    }
}
