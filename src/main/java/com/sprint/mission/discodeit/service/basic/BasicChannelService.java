package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
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
                .filter(channel -> channel.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
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

        List<User> users = new ArrayList<>(channel.getUsers());

        leaveChannelFromUsers(channel, users);

        users.forEach(userRepository::save);

        channelRepository.delete(channel);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannelById(channelId);
        User user = userRepository.findUserById(userId);

        channel.join(user);
        user.join(channel);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannelById(channelId);
        User user = userRepository.findUserById(userId);

        channel.leave(user);
        user.leave(channel);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    private void existsByChannelName(String name) {
        boolean exist = channelRepository.findAllChannel().stream()
                .anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }

    private void leaveChannelFromUsers(Channel channel, List<User> users) {
        for (User user : users) {
            channel.leave(user);
            user.leave(channel);
        }
    }
}
