package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final List<Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        this.userService = userService;
        this.data = new ArrayList<>();
    }

    @Override
    public Channel createChannel(String name) {
        existsByChannelName(name);
        Channel channel = new Channel(name);
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
        return userService.findUserById(userId).getChannels();
    }

    @Override
    public Channel updateChannel(UUID channelId, String name) {
        Channel channel = findChannelById(channelId);
        existsByChannelName(name);
        channel.update(name);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Channel channel = findChannelById(channelId);

        channel.getMessages().forEach(message -> {
            message.getUser().delete(message);
            channel.delete(message);
        });
        channel.getUsers().forEach(user -> {
            user.leave(channel);
            channel.leave(user);
        });
        data.remove(channel);
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);
        channel.join(user);
        user.join(channel);
    }

    @Override
    public void leaveChannel(UUID userId, UUID channelId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);
        user.leave(channel);
        channel.leave(user);
    }

    //채널명 중복체크
    private void existsByChannelName(String name) {
        boolean exist = data.stream().anyMatch(channel -> channel.getChannelName().equals(name));
        if (exist) {
            throw new IllegalArgumentException("이미 사용중인 채널명입니다: " + name);
        }
    }
}
