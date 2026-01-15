package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final UserService userService;
    private final List<Channel> channels = new ArrayList<>();

    // 채널 참여, 퇴장을 위해 userService 참조
    public JCFChannelService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Channel create(String channelName) {
        Channel newChannel = new Channel(channelName);
        channels.add(newChannel);
        return newChannel;
    }

    @Override
    public Channel findById(UUID uuid) {
        return channels.stream()
                .filter(c -> c.getId().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. ID: "  + uuid));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels);
    }

    @Override
    public Channel update(UUID id, String updateChannelName) {
        Channel updateChannel = findById(id);
        updateChannel.updateChannel(updateChannelName);
        return updateChannel;
    }

    @Override
    public void delete(UUID uuid) {
        Channel channel = findById(uuid);
        channels.remove(channel);
    }

    @Override
    public void addMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.addMember(user);
    }

    @Override
    public void removeMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.removeMember(user);
    }

    @Override
    public List<Channel> findMyChannels(UUID userId) {
        User user = userService.findById(userId);

        return user.getMyChannels();
    }
}
