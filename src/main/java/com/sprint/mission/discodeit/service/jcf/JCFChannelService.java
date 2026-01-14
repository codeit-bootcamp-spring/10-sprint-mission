package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channelMap = new HashMap<>();

    // 연관 도메인 서비스
    private MessageService messageService;

    // Setter 주입, 순환 참조 문제 회피
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    // Create
    @Override
    public Channel createChannel(String name, boolean isPublic) {
        // 엔티티에서 빈 이름의 채널 확인함
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
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    // Update
    @Override
    public Channel updateChannel(UUID channelId, String name) {
        Channel channel = getChannelOrThrow(channelId);

        channel.updateName(name); // 엔티티에서 빈 채널 이름 검증함.

        return channel;
    }

    @Override
    public Channel updateChannelVisibility(UUID channelId, boolean isPublic) {
        Channel channel = getChannelOrThrow(channelId);
        channel.updatePublic(isPublic);

        return channel;
    }

    // Delete
    @Override
    public Channel deleteChannel(UUID channelId) {
        Channel channel = getChannelOrThrow(channelId);

        messageService.deleteMessagesByChannelId(channelId); // 채널 내 모든 메시지 삭제
        channel.getUsers().forEach(user -> {user.leaveChannel(channel);}); // 채널 내 유저에게서 채널 삭제
        channelMap.remove(channelId); // 채널 맵에서 삭제

        return channel;
    }

    // Helper
    private Channel getChannelOrThrow(UUID channelId) {
        return findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다. ID: " + channelId));
    }
}