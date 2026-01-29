package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    public Channel createChannel(UUID requestId, String channelName) {
        User requester = findUserByUserID(requestId);

        Channel channel = new Channel(
                requester,
                channelName
        );

        userRepository.save(channel.getOwner());
        channelRepository.save(channel);
        return channel;
    }

    public Channel findChannelByChannelId(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }

    public List<Channel> findAllChannels() {
        return channelRepository.findAll();
    }

    public Channel updateChannelName(UUID requestId, UUID channelId, String channelName) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);

        channel.validateChannelOwner(requester);
        channel.updateName(channelName);

        channel.getMembers().stream().forEach(user -> userRepository.save(user));
        channel.getMessages().stream().forEach(message -> messageRepository.save(message));

        channelRepository.save(channel);
        return channel;
    }

    public void addChannelMember(UUID requestId, UUID channelId, UUID memberId) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);
        User member = findUserByUserID(memberId);

        channel.validateChannelOwner(requester);

        channel.addMember(member);
        member.addChannel(channel);

        channel.getMembers().stream().forEach(user -> userRepository.save(user));
        channel.getMessages().stream().forEach(message -> messageRepository.save(message));

        channelRepository.save(channel);
    }

    public void deleteChannel(UUID requestId, UUID channelId) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);

        channel.validateChannelOwner(requester);

        for (Message message : new ArrayList<>(channel.getMessages())) {
            User user = findUserByUserID(message.getSenderId());
            user.removeMessage(message);
            userRepository.save(user);

            channel.removeMessage(message);

            messageRepository.delete(message);
        }

        for (User u : new ArrayList<>(channel.getMembers())) {
            User user = findUserByUserID(u.getId());
            user.removeChannel(channel);
            userRepository.save(user);

            channel.removeMember(user);
        }

        channelRepository.save(channel);
        channelRepository.delete(channel);
    }

    private User findUserByUserID(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private Message findMessageByMessageId(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지를 찾을 수 없습니다 messageId: " + id));
    }
}
