package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    @Override
    public Channel createChannel(String channelName) {
        if (channelRepository.findAll().stream().anyMatch(channel -> channel.getChannelName().equals(channelName))) {
            throw new IllegalArgumentException("같은 이름의 채널이 존재합니다. " + channelName);
        }
        Channel channel = new Channel(channelName);
        channelRepository.save(channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    @Override
    public Channel findId(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    @Override
    public Channel findName(String name) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> findChannels(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getUserList().stream().anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID channelId, String channelName) {
        Channel foundChannel = findId(channelId);
        Optional.ofNullable(channelName)
                .filter(n -> !n.trim().isEmpty())
                .ifPresent(foundChannel::setChannelName);
        channelRepository.save(foundChannel);
        return foundChannel;
    }

    @Override
    public void delete(UUID channelId) {
        channelRepository.delete(channelId);
    }

    @Override
    public void deleteAll() {
        channelRepository.deleteAll();
        System.out.println("모든 채널 데이터를 초기화했습니다.");
    }
}
