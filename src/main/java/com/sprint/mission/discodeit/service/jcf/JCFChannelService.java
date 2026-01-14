package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String title, String description) {
        validateDuplicateTitle(title);

        Channel channel = new Channel(title, description);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID uuid) {
        return Optional.ofNullable(data.get(uuid))
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
    }

    @Override
    public Optional<Channel> findChannelByTitle(String title) {
        return data.values().stream()
                .filter(c -> Objects.equals(c.getTitle(), title))
                .findFirst();
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateChannel(UUID uuid, String title, String description) {
        Channel channel = getChannel(uuid);
        // title 중복성 검사
        if (title != null && !Objects.equals(channel.getTitle(), title))
            validateDuplicateTitle(title);

        Optional.ofNullable(title).ifPresent(channel::updateTitle);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);
        channel.updateUpdatedAt();

        return channel;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Channel channel = getChannel(uuid);
        data.remove(channel.getId());
    }

    @Override
    public void deleteChannelByTitle(String title) {
        Channel channel = findChannelByTitle(title).orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        data.remove(channel.getId());
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);
        channel.addParticipant(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);
        channel.removeParticipant(user);
    }

    private void validateDuplicateTitle(String title) {
        findChannelByTitle(title).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 채널명입니다"); });
    }
}
