package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository, MessageRepository messageRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        existsByChannelName(name);
        Channel channel = new Channel(type, name, description);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return channelRepository.findChannelById(channelId);
    }

    @Override
    public Channel findChannelByName(String name) {
        return channelRepository.findAllChannel().stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAllChannel();
    }

    @Override
    public List<Channel> findChannelsByUser(UUID userId) {
        return channelRepository.findAllChannel().stream()
                .filter(channel -> channel.getUserIds().stream()
                        .anyMatch(u -> u.equals(userId)))
                .toList();
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannelById(channelId);
        User user = userRepository.findUserById(userId);

        channel.addUser(userId);
        user.addChannel(channelId);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannelById(channelId);
        User user = userRepository.findUserById(userId);

        channel.deleteUser(userId);
        user.deleteChannel(channelId);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        Channel channel = channelRepository.findChannelById(channelId);

        if (name != null && !name.equals(channel.getChannelName())) {
            existsByChannelName(name);
        }

        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);

        channelRepository.save(channel);

        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = channelRepository.findChannelById(channelId);

        for (UUID userId : channel.getUserIds()) {
            User user = userRepository.findUserById(userId);
            user.deleteChannel(channelId);
            userRepository.save(user);
        }

        channelRepository.delete(channel);
    }

    //채널명 중복체크
    private void existsByChannelName(String name) {
        boolean exist = channelRepository.findAllChannel().stream()
                .anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }
}
