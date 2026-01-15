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
    public Channel createChannel(String name, String description, Channel.ChannelVisibility visibility) {
        // 엔티티에서 빈 이름의 채널 확인함
        Channel newChannel = new Channel(name, description, visibility);
        channelMap.put(newChannel.getId(), newChannel);

        return newChannel;
    }

    // Read
    @Override
    public Channel findById(UUID channelId) {
        Channel channel = channelMap.get(channelId);

        if (channel == null) {
            throw new NoSuchElementException("채널을 찾을 수 없습니다: " + channelId);
        }

        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channelMap.values());
    }

    // Update
    @Override
    public Channel updateChannel(UUID channelId, String newName, String description, Channel.ChannelVisibility newVisibility) {
        Channel channel = findById(channelId);

        Optional.ofNullable(newName)
                .filter(name -> !name.equals(channel.getChannelName()))
                .ifPresent(channel::updateName);

        Optional.ofNullable(description)
                .filter(name -> !name.equals(channel.getDescription()))
                .ifPresent(channel::updateDescription);

        Optional.ofNullable(newVisibility)
                .filter(v -> v != channel.getChannelVisibility())
                .ifPresent(channel::updateVisibility);

        return channel;
    }
    // Delete
    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = findById(channelId);

        messageService.deleteMessagesByChannelId(channelId); // 채널 내 모든 메시지 삭제
        channel.getUsers().forEach(user -> {user.leaveChannel(channel);}); // 채널 내 유저에게서 채널 삭제
        channelMap.remove(channelId); // 채널 맵에서 삭제

    }

}