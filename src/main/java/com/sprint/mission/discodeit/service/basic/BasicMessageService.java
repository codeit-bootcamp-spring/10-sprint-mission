package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.List;
import java.util.UUID;

public class BasicMessageService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicMessageService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    public Message createMessage(UUID requestId, UUID channelId, String content) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);

        Message message = new Message(requester, channel, content);

        syncMessageToAllCopies(requester.getId(), channel.getId(), message);
        userRepository.save(message.getSender());
        channelRepository.save(message.getChannel());
        messageRepository.save(message);

        return message;
    }

    private void syncMessageToAllCopies(UUID senderId, UUID channelId, Message message) {
        // 1) Channel 복사본 전체 중 channelId가 같은 것들에 메시지 반영
        for (Channel c : channelRepository.findAll()) {
            if (!c.getId().equals(channelId)) continue;

            // 채널 자체 메시지 갱신
            safeAddMessageToChannel(c, message);

            // 채널 내부 유저 복사본 중 sender도 갱신 (channel.users 안의 sender 복사본)
            for (User u : c.getMembers()) {
                if (u.getId().equals(senderId)) {
                    safeAddMessageToUser(u, message);
                }
            }

            channelRepository.save(c);
        }

        // 2) User 복사본 전체 중 senderId가 같은 것들에 메시지 반영
        for (User u : userRepository.findAll()) {
            if (!u.getId().equals(senderId)) continue;

            // 유저 자체 메시지 갱신
            safeAddMessageToUser(u, message);

            // 유저 내부 채널 복사본 중 channel도 갱신 (user.channels 안의 channel 복사본)
            for (Channel c : u.getChannels()) {
                if (c.getId().equals(channelId)) {
                    safeAddMessageToChannel(c, message);
                }
            }

            userRepository.save(u);
        }
    }

    private void safeAddMessageToChannel(Channel channel, Message message) {
        boolean exists = channel.getMessages().stream()
                .anyMatch(m -> m.getId().equals(message.getId()));

        if (!exists) {
            channel.getMessages().add(message);
        }
    }

    private void safeAddMessageToUser(User user, Message message) {
        boolean exists = user.getMessages().stream()
                .anyMatch(m -> m.getId().equals(message.getId()));

        if (!exists) {
            user.getMessages().add(message);
        }
    }

    public Message findMessageByMessageId(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지를 찾을 수 없습니다 messageId: " + id));
    }

    public List<Message> findAllMessages() {
        return messageRepository.findAll();
    }

    public Message updateMessage(UUID requestId, UUID messageId, String content) {
        User requester = findUserByUserID(requestId);
        Message message = findMessageByMessageId(messageId);

        message.validateOwner(requester);
        message.updateContent(content);

        userRepository.save(message.getSender());
        channelRepository.save(message.getChannel());
        messageRepository.save(message);

        return message;
    }

    public void deleteMessage(UUID requestId, UUID messageId) {
        User requester = findUserByUserID(requestId);
        Message message = findMessageByMessageId(messageId);

        message.validateOwner(requester);

        message.clear();

        channelRepository.save(message.getChannel());
        userRepository.save(message.getSender());

        messageRepository.delete(message);
    }

    private User findUserByUserID(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Channel findChannelByChannelId(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }
}
