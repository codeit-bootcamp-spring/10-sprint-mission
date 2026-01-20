package com.sprint.mission.service.basic;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;
import com.sprint.mission.repository.ChannelRepository;
import com.sprint.mission.repository.UserRepository;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.UserService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final UserService userService;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository, UserService userService) {
        this.userService = userService;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel create(UUID userID, String name) {
        User user = userService.getUserOrThrow(userID);
        Channel channel = new Channel(user, name);
        user.joinChannel(channel);

        userRepository.save(user);
        return channelRepository.save(channel);
    }

    @Override
    public Channel update(UUID channelId, String name) {
        Channel channel = getChannelOrThrow(channelId);
        validateChangeChannelNameNotDuplicate(channelId, name);
        channel.updateName(name);

        List<User> users = userRepository.findByChannelId(channelId);
        for (User user : users) {
            user.updateChannel(channelId, name);
            userRepository.save(user);
        }
        return channelRepository.save(channel);
    }

    @Override
    public Channel joinChannel(UUID userId, UUID channelId) {
        User user = userService.getUserOrThrow(userId);
        Channel channel = getChannelOrThrow(channelId);
        bidirectionalJoin(user, channel);

        userRepository.save(user);
        return channelRepository.save(channel);
    }

    @Override
    public Channel leaveChannel(UUID userId, UUID channelId) {
        User user = userService.getUserOrThrow(userId);
        Channel channel = getChannelOrThrow(channelId);
        bidirectionalLeave(user, channel);

        userRepository.save(user);
        return channelRepository.save(channel);
    }

    @Override
    public Channel getChannelOrThrow(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> getAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);
        channelRepository.deleteById(channelId);
    }

    @Override
    public List<Channel> getUserChannels(UUID userId) {
        return channelRepository.findByUserId(userId);
    }

    private void validateChangeChannelNameNotDuplicate(UUID channelId, String name) {
        String trimmedName = name.trim();
        boolean isDuplicated = channelRepository.findAll().stream()
                .anyMatch(channel ->
                        !channel.getId().equals(channelId) &&
                                channel.getName().equals(trimmedName));

        if (isDuplicated) {
            throw new IllegalArgumentException("이미 존재하는 채널이름 입니다.");
        }
    }

    private void bidirectionalJoin(User user, Channel channel) {
        user.joinChannel(channel);
        channel.joinUser(user);
    }

    private void bidirectionalLeave(User user, Channel channel) {
        user.leaveChannel(channel);
        channel.leaveUser(user);
    }
}
