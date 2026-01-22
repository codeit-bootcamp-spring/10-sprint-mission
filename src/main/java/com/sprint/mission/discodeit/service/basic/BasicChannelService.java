package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AlreadyJoinedChannelException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.DuplicationChannelException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public BasicChannelService(ChannelRepository channelRepository,  UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel createChannel(String channelName) {
        if (channelName == null || channelName.isBlank()) {
            throw new ChannelNotFoundException();
        }
        if (channelRepository.existsByNameChannel(channelName)) {
            throw new DuplicationChannelException();
        }
        Channel channel = new Channel(channelName);
        return channelRepository.createChannel(channel);
    }

    @Override
    public Channel findChannel(UUID channelId) {
        return channelRepository.findChannel(channelId);
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAllChannel();
    }

    @Override
    public Channel userAddChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannel(channelId);
        User user = userRepository.findUser(userId);

        if (channel.hasChannelUser(user)) {
            throw new AlreadyJoinedChannelException();
        }

        channel.addChannelUser(user);

        channelRepository.updateChannel(channelId, channel);
        return channel;
    }

    @Override
    public Channel userRemoveChannel(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findChannel(channelId);
        User user = userRepository.findUser(userId);

        if (!channel.hasChannelUser(user)) {
            throw new UserNotFoundException();
        }

        channel.removeChannelUser(user);

        channelRepository.updateChannel(channelId, channel);
        return channel;
    }

    @Override
    public Channel nameUpdateChannel(UUID channelId, String channelName) {
        Channel channel = channelRepository.findChannel(channelId);

        if (channelName == null || channelName.isEmpty()) {
            throw new ChannelNotFoundException();
        }

        channel.updateChannelName(channelName);

        channelRepository.updateChannel(channelId, channel);
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        Channel channel = channelRepository.findChannel(channelId);
        channelRepository.deleteChannel(channelId);
        return channel;
    }

    @Override
    public Channel findByUserChannel(UUID userId) {
        userRepository.findUser(userId);
        return channelRepository.findByUserId(userId);
    }

    // **특정 채널 참가자 목록 조회**
    @Override
    public String findAllUserInChannel(UUID channelId) {
        Channel channel = channelRepository.findChannel(channelId);
        List<User> users = channel.getChannelUser();

        return users.stream()
                .map(User::getUserName)
                .collect(Collectors.joining(", "));
    }

}
