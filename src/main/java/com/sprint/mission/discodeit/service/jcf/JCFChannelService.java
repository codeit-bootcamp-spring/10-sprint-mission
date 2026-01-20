package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final MessageService messageService;
    private final UserService userService;
    private final Map<UUID, Channel> data = new HashMap<>();

    public JCFChannelService(MessageService messageService, UserService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        data.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public List<Channel> getChannelList() {
        return data.values().stream().toList();
    }

    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        // 유저가 존재하지 않을 경우 예외 처리
        if (userService.getUserInfoByUserId(userId) == null) {
            throw new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다.");
        }

        return data.values().stream()
                .filter(channel ->
                        channel.getJoinedUsers().stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel getChannelInfoById(UUID channelId) {
        return findChannelById(channelId);
    }

    @Override
    public Channel updateChannelName(UUID channelId, String newChannelName) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        Channel channel = findChannelById(channelId);
        if (channel.getChannelName().equals(newChannelName)) {
            throw new IllegalArgumentException("해당 채널의 이름이 바꿀 이름과 동일합니다.");
        }
        channel.updateChannelName(newChannelName);
        // 변경된 객체 반환
        return channel;
    }

    @Override
    public void joinChannel(UUID channelId, UUID userID) {
        Objects.requireNonNull(userID, "userId값은 null일 수 없습니다.");

        Channel channel = findChannelById(channelId);
        User user = userService.getUserInfoByUserId(userID);

        channel.addUser(user);
        user.updateJoinedChannels(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userID) {
        Objects.requireNonNull(userID, "userId값은 null일 수 없습니다.");

        Channel channel = findChannelById(channelId);
        User user = userService.getUserInfoByUserId(userID);

        channel.removeUser(user.getId());
        user.removeChannel(channel);
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = findChannelById(channelId);

        messageService.clearChannelMessage(channelId);
        data.remove(channelId);
    }

    private Channel findChannelById(UUID channelId) {
        Objects.requireNonNull(channelId, "channelId는 null일 수 없습니다.");

        Channel channel = data.get(channelId);

        if (channel == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        return channel;
    }
}
