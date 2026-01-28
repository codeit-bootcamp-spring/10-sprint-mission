package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final UserService userService;
    private final List<Channel> data;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new ArrayList<>();
    }

    @Override
    public Channel create(ChannelType type, String name, String description) {
        existsByChannelName(name);
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        return data.stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public Channel findChannelByName(String name) {
        return data.stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(data);
    }

    //특정 사용자의 참가한 채널 리스트 조회
    @Override
    public List<Channel> findChannelsByUser(UUID userId) {
        return data.stream()
                .filter(channel -> channel.getUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID channelId, String name, String description) {
        Channel channel = findChannelById(channelId);

        if (name != null && !name.equals(channel.getChannelName())) {
            existsByChannelName(name);
        }

        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findChannelById(channelId);

        new ArrayList<>(channel.getUsers()).forEach(user -> {
            user.leave(channel);
            channel.leave(user);
        });
        data.remove(channel);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channel.join(user);
        user.join(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channel.leave(user);
        user.leave(channel);
    }

    //채널명 중복체크
    private void existsByChannelName(String name) {
        boolean exist = data.stream().anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }
}
