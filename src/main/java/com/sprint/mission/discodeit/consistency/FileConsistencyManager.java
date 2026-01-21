package com.sprint.mission.discodeit.consistency;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class FileConsistencyManager {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public FileConsistencyManager(UserRepository userRepository,
                                  ChannelRepository channelRepository,
                                  MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    public User saveUser(User user) {
        userRepository.saveUser(user);
        user.getChannels().forEach(channelRepository::saveChannel);
        user.getMessages().forEach(messageRepository::saveMessage);

        return user;
    }

    public Channel saveChannel(Channel channel) {
        channelRepository.saveChannel(channel);
        channel.getUsers().forEach(userRepository::saveUser);
        channel.getMessages().forEach(messageRepository::saveMessage);

        return channel;
    }

    public Message saveMessage(Message message) {
        messageRepository.saveMessage(message);
        userRepository.saveUser(message.getUser());
        channelRepository.saveChannel(message.getChannel());

        return message;
    }

    public void deleteUser(User user) {
        List<Channel> channels = new ArrayList<>(user.getChannels());
        List<Message> messages = new ArrayList<>(user.getMessages());

        for (Channel channel : channels) {
            channel.removeUser(user);
            channelRepository.saveChannel(channel);
        }

        for (Message message : messages) {
            message.removeFromChannelAndUser();
            messageRepository.deleteMessage(message.getId());
        }

        userRepository.deleteUser(user.getId());
    }

    public void deleteChannel(Channel channel) {
        // 채널의 유저 목록과 메시지 목록 조회
        List<User> users = new ArrayList<>(channel.getUsers());
        List<Message> messages = new ArrayList<>(channel.getMessages());

        // 채널에 가입된 유저들 탈퇴 처리
        for (User user : users) {
            user.leaveChannel(channel);
            userRepository.saveUser(user);
        }
        // 채널에 작성된 메시지 삭제 처리
        for (Message message : messages) {
            message.removeFromChannelAndUser();
            messageRepository.deleteMessage(message.getId());
        }

        // 채널 삭제
        channelRepository.deleteChannel(channel.getId());
    }

    public void deleteMessage(Message message) {
        userRepository.saveUser(message.getUser());
        channelRepository.saveChannel(message.getChannel());
        messageRepository.deleteMessage(message.getId());
    }

    public void channelManagement(Channel channel, User user) {
        channelRepository.saveChannel(channel);
        userRepository.saveUser(user);
    }
}
