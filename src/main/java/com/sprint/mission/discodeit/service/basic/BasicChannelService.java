package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    private final UserService userService;
    private MessageService messageService;

    public BasicChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void save(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public Channel create(String channelName) {
        Channel newChannel = new Channel(channelName);
        return channelRepository.save(newChannel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. ID: "  + id));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        User user = userService.findById(userId);
        return user.getMyChannels();
    }

    @Override
    public Channel update(UUID id, String updateChannelName) {
        Channel updateChannel = findById(id);
        updateChannel.updateChannel(updateChannelName);
        return channelRepository.save(updateChannel);
    }

    public void addChannelByUserId(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.addMember(user);
        user.addMyChannel(channel);
        channelRepository.save(channel);
        userService.save(user);
    }

    @Override
    public void removeChannelByUserId(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.removeMember(user);
        user.removeMyChannel(channel);
        channelRepository.save(channel);
        userService.save(user);
    }

    @Override
    public void deleteChannelByChannelId(UUID channelId) {
        Channel channel = findById(channelId);
        messageService.deleteMessagesByChannelId(channelId);
        new ArrayList<>(channel.getMembers()).forEach(user -> {
            user.removeMyChannel(channel);
            userService.save(user);
        });
        channelRepository.deleteById(channelId);
    }
}
