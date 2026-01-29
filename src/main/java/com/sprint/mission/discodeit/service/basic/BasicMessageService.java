package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository, UserService userService, ChannelService channelService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String message) {
        Channel channel = channelService.getChannel(channelId);
        User user = userService.getUser(userId);

        // 참여여부 확인
        if (channel.getParticipants().stream().noneMatch(u -> Objects.equals(u.getId(), user.getId()))) {
            throw new IllegalStateException("채널에 소속되지 않은 유저입니다.");
        }

        Message msg = new Message(channel, user, message);
        messageRepository.save(msg);
        channel.addMessage(msg);
        channelService.updateChannel(channel);
        user.addMessageHistory(msg);
        userService.updateUser(user);

        return msg;
    }

    @Override
    public Message getMessage(UUID uuid) {
        return messageRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID uuid) {
        Channel channel = channelService.getChannel(uuid);
        return channel.getMessages();
    }

    @Override
    public List<Message> findAllMessages() {
        return messageRepository.findAll().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
                .toList();
    }

    @Override
    public Message updateMessage(UUID uuid, String newMessage) {
        Message msg = getMessage(uuid);

        Optional.ofNullable(newMessage).ifPresent(msg::updateMessage);
        msg.updateUpdatedAt();
        messageRepository.save(msg);

        // 다른 객체도 변경
        userService.findAllUsers().stream()
                .filter(u -> Objects.equals(u.getId(), msg.getUser().getId()))
                .findFirst()
                .ifPresent(u -> {
                    u.updateMessageHistory(msg);
                    userService.updateUser(u);
                });
        channelService.findAllChannels().stream()
                .filter(c -> Objects.equals(c.getId(), msg.getChannel().getId()))
                .findFirst()
                .ifPresent(c -> {
                    c.updateMessage(msg);
                    channelService.updateChannel(c);
                });

        return msg;
    }

    @Override
    public Message updateMessage(Message newMessage) {
        newMessage.updateUpdatedAt();
        return messageRepository.save(newMessage);
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message msg = getMessage(uuid);
        Channel channel = channelService.getChannel(msg.getChannel().getId());
        User user = userService.getUser(msg.getUser().getId());

        user.removeMessageHistory(msg);
        userService.updateUser(user);
        channel.removeMessage(msg);
        channelService.updateChannel(channel);
        messageRepository.deleteById(uuid);
    }
}
