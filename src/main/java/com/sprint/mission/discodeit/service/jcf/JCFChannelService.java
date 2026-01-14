package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channelMap = new HashMap<>();

    private MessageService messageService;

    // Create
    @Override
    public Channel createChannel(String name, boolean isPublic) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어있을 수 없습니다.");
        }
        Channel newChannel = new Channel(name, isPublic);
        channelMap.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    // Read
    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(channelMap.get(channelId));
    }

    @Override
    public List<Channel> findChannelsAccessibleBy(User user) {
        return channelMap.values().stream()
                .filter(channel -> channel.isAccessibleBy(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    // Update
    @Override
    public void updateChannel(UUID channelId, String name) {
        Channel channel = getChannelOrThrow(channelId);
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("채널 이름은 비어있을 수 없습니다.");
        }
        channel.updateName(name);
    }

    @Override
    public void updateChannelVisibility(UUID channelId, boolean isPublic) {
        Channel channel = getChannelOrThrow(channelId);
        channel.updatePublic(isPublic);
    }

    // Delete
    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);
        messageService.deleteMessagesByChannelId(channelId);
        channelMap.remove(channelId);
    }

    // Helper
    private Channel getChannelOrThrow(UUID channelId) {
        return findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다. ID: " + channelId));
    }

    // Setter
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}