package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository){
        this.channelRepository = channelRepository;
    }
    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        return channelRepository.save(channel);
    }

    @Override
    public Channel read(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("해당 ID의 채널을 찾을 수 없습니다."));
    }

    @Override
    public List<Channel> readAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        List<Channel> channelList = channelRepository.findAll();
        return channelList.stream()
                .filter(u -> u.getUserList().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = read(id);
        channel.update(name);
        return channelRepository.save(channel);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = read(id);
        channelRepository.delete(id);

    }

    @Override
    public void deleteUserInChannels(UUID userId) {
        List<Channel> channelList = channelRepository.findAll();
        channelList.forEach(channel -> {
            boolean removed = channel.getUserList().removeIf(user ->
                    user.getId().equals(userId));
            if(removed) {
                channelRepository.save(channel);
            }
        });
    }
}
