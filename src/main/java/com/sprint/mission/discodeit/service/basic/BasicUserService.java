package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BasicUserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    public User createUser(CreateUserRequest createUserRequest) {
        User user = new User(
                createUserRequest.nickName(),
                createUserRequest.userName(),
                createUserRequest.email(),
                createUserRequest.phoneNumber()
        );

        userRepository.save(user);
        return user;
    }

    public User findUserByUserID(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(UUID requestId, UpdateUserRequest updateUserRequest) {
        User user = findUserByUserID(requestId);

        user.update(
                updateUserRequest.nickName(),
                updateUserRequest.userName(),
                updateUserRequest.email(),
                updateUserRequest.phoneNumber()
        );

        user.getMessages().stream().forEach(message -> messageRepository.save(message));
        user.getChannels().stream().forEach(channel -> channelRepository.save(channel));

        userRepository.save(user);
        return user;
    }

    public void deleteUser(UUID requestId) {
        User user = findUserByUserID(requestId);

        Set<Channel> channels = user.getChannels();
        user.validateChannelOwner();

        //유저가 보낸 메세지 삭제하고 채널에도 해당 메세지 삭제
        for (Message message : new ArrayList<>(user.getMessages())) {
            message.clear();
            Channel channel = message.getChannel();
            channelRepository.save(channel);
            messageRepository.delete(message);
        }

        //유저가 들어있는 채널에서 유저 삭제
        for (Channel channel : new ArrayList<>(user.getChannels())) {
            channel.removeMember(user);
            channelRepository.save(channel);

            user.removeChannel(channel);
            channelRepository.save(channel);

        }

        userRepository.delete(user);
    }

    private Channel findChannelByChannelId(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }

    private Message findMessageByMessageId(UUID id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지를 찾을 수 없습니다 messageId: " + id));
    }
}
