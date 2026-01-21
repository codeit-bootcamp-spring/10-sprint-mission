package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;
    private MessageService messageService;

    public BasicChannelService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository, UserService userService) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String name, String type) {
        validateChannelInput(name, type);
        Channel channel = new Channel(name, type);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID id, String name, String type) {
        Channel channel = getChannel(id);
        Optional.ofNullable(name).filter(n -> !n.isBlank()).ifPresent(channel::updateName);
        Optional.ofNullable(type).filter(t -> !t.isBlank()).ifPresent(channel::updateType);
        channelRepository.save(channel);
        channel.getUsers().forEach(user -> {
            user.removeChannel(channel);
            user.addChannel(channel);
            userRepository.save(user);
        });
        channel.getMessages().forEach(message -> messageRepository.save(message));
        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel channel = getChannel(id);
        if(messageService != null) {
            messageService.getMessagesByChannelId(id).forEach(m -> messageService.deleteMessage(m.getId()));
        }
        new ArrayList<>(channel.getUsers()).forEach(user -> {
            user.removeChannel(channel);
            userRepository.save(user);
        });
        channelRepository.delete(id);
    }

    @Override
    public void enterChannel(UUID userId, UUID channelId) {
        User user = userService.getUser(userId);
        Channel channel = getChannel(channelId);
        if (channel.getUsers().contains(user)) throw new IllegalArgumentException("이미 해당 채널에 참가 중입니다.");
        channel.addUser(user);
        user.addChannel(channel);
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.getUser(userId);
        Channel channel = getChannel(channelId);
        if (!channel.getUsers().contains(user)) throw new IllegalArgumentException("참가하고 있지 않은 채널입니다.");

        if (messageService != null) {
            channel.getMessages().stream()
                    .filter(m -> m.getUser().equals(user))
                    .toList()
                    .forEach(m -> messageService.deleteMessage(m.getId()));
        }
        channel.removeUser(user);
        user.removeChannel(channel);
        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        User user = userService.getUser(userId);
        return new ArrayList<>(user.getChannels());
    }
    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        Channel channel = getChannel(channelId);
        return new ArrayList<>(channel.getUsers());
    }

    public void validateChannelInput(String name, String type){
        if (name == null || name.isBlank()) throw new IllegalArgumentException("채널 이름은 필수입니다.");
        if (type == null || type.isBlank()) throw new IllegalArgumentException("채널 타입은 필수입니다.");
    }
}
