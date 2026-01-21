package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private ChannelRepository channelRepository;
    private List<Channel> channelList;

    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
        this.channelList = new ArrayList<>();
    }

    @Override
    public Channel create(String channelName) {
        Channel channel = new Channel(channelName);
        this.channelRepository.save(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        for (Channel channel : this.channelRepository.loadAll()) {
            if (channel.getId().equals(id)) {
                return channel;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Channel> readAll() {
        return this.channelRepository.loadAll();
    }

    @Override
    public Channel updateChannelname(UUID id, String name) {
        this.channelRepository.updateChannelname(id, name);
        return this.read(id);
    }

    @Override
    public void delete(UUID id) {
        this.channelRepository.delete(id);
    }

    // 유저 참가
    public void joinUser(UUID userId, UUID channelId, BasicUserService userService) {
        User user = userService.read(userId);
        Channel channel = this.read(channelId);
        user.getChannelList().add(channel);
        channel.getUserList().add(user);
    }

    // 유저 탈퇴
    public void quitUser(UUID userId, UUID channelId, BasicUserService userService) {
        User user = userService.read(userId);
        Channel channel = this.read(channelId);
        user.getChannelList().remove(channel);
        channel.getUserList().remove(user);
    }

    // 특정 유저가 참가한 채널 리스트 조회
    public List<Channel> readUserChannelList(UUID userId, BasicUserService userService) {
        User user = userService.read(userId);
        return user.getChannelList();
    }
}