package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

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
        channelRepository.createChannel(channel); // 저장
        return channel;
    }

    @Override
    public Channel nameUpdateChannel(UUID channelId, String channelName) {
        Channel channel = channelRepository.findChannel(channelId);

        if (channelName == null || channelName.isEmpty()) {
            throw new ChannelNotFoundException();
        }

        channel.updateChannelName(channelName);
        channelRepository.createChannel(channel); // 저장
        return channel;
    }

    @Override
    public Channel deleteChannel(UUID channelId) {
        channelRepository.deleteChannel(channelId);
        return null; // 삭제 후 반환 필요 없으면 null
    }

    @Override
    public Channel findByUserChannel(UUID userId) {
        userRepository.findUser(userId);
        return channelRepository.findByUserId(userId);
    }

    @Override
    public String findAllUserInChannel(UUID channelId) {
        return channelRepository.findAllUserInChannel(channelId);
    }
}
