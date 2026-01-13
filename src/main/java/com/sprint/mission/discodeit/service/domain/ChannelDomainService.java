package com.sprint.mission.discodeit.service.domain;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class ChannelDomainService {
    private final ChannelService channelService;
    private final MessageService messageService;
    private final UserService userService;

    public ChannelDomainService(ChannelService channelService, MessageService messageService, UserService userService) {
        this.channelService = channelService;
        this.messageService = messageService;
        this.userService = userService;
    }

    public Channel createChannel(UUID requestId, String channelName) {
        User requester = findUserByUserID(requestId);

        Channel channel = new Channel(
                requester,
                channelName
        );

        channelService.save(channel);
        return channel;
    }

    public Channel findChannelByChannelId(UUID id) {
        return channelService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널을 찾을 수 없습니다 channelId: " + id));
    }

    public List<Channel> findAllChannels() {
        return channelService.findAll();
    }

    public Channel updateChannelName(UUID requestId, UUID channelId, String channelName) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);

        channel.validateChannelOwner(requester);
        channel.updateName(channelName);

        return channel;
    }

    public void addChannelMember(UUID requestId, UUID channelId, UUID memberId) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);
        User member = findUserByUserID(memberId);

        channel.validateChannelOwner(requester);
        channel.addMember(member);
    }

    public void deleteChannel(UUID requestId, UUID channelId) {
        User requester = findUserByUserID(requestId);
        Channel channel = findChannelByChannelId(channelId);

        channel.validateChannelOwner(requester);

        channel.clear();
        channelService.delete(channel);
        messageService.deleteByChannel(channel);
    }

    private User findUserByUserID(UUID userId) {
        return userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다 userId: " + userId));
    }
}
