package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    BasicChannelService(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Channel createChannel(String channelName, ChannelType channelType, String description) {
        validateChannelExist(channelName);
        Channel channel = new Channel(channelName, channelType, description);
        channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다."));
    }

    @Override
    public List<Channel> getAllChannels() {
        return List.copyOf(channelRepository.findAll());
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        return List.copyOf(channelRepository.findAllByUserId(userId));
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName, ChannelType channelType, String description) {
        Channel findChannel = getChannel(channelId);
        Optional.ofNullable(channelName)
                .ifPresent(findChannel::updateChannelName);
        Optional.ofNullable(channelType)
                .ifPresent(findChannel::updateChannelType);
        Optional.ofNullable(description)
                .ifPresent(findChannel::updateDescription);
        channelRepository.save(findChannel);
        return findChannel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannel(channelId);
        userRepository.findAllByChannelId(channelId).forEach(user -> {
            user.removeChannelId(channelId);
            userRepository.save(user);
        });
        channelRepository.deleteById(channelId);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));

        channel.addUserId(userId);
        user.addChannelId(channelId);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저가 존재하지 않습니다."));

        channel.removeUserId(userId);
        user.removeChannelId(channelId);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    private void validateChannelExist(String channelName) {
        if(channelRepository.findByName(channelName).isPresent())
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
    }
}
