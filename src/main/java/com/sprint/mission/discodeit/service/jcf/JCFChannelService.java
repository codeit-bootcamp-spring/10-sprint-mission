package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    public JCFChannelService(UserService userService) {
        data = new HashMap<>();
    }

    @Override
    public Channel createChannel(String title, String description) {
        findChannelByTitle(title).orElseThrow(() -> new IllegalStateException("이미 존재하는 채널입니다"));
        Channel channel = new Channel(title, description);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findChannel(UUID uuid) {
        return Optional.ofNullable(data.get(uuid));
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
        Channel channel = findChannel(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        // title 중복성 검사
        if (!Objects.equals(channel.getTitle(), title)) {
            findChannelByTitle(title).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 채널입니다"); });
        }

        boolean isChanged = false;
        if (!Objects.equals(channel.getTitle(), title)) {
            channel.updateTitle(title);
            isChanged = true;
        }
        if (!Objects.equals(channel.getDescription(), description)) {
            channel.updateDescription(description);
            isChanged = true;
        }

        if (isChanged) {
            channel.updateUpdatedAt();
        }

        return channel;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Channel channel = findChannel(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        data.remove(channel.getId());
    }

    @Override
    public void deleteChannelByTitle(String title) {
        Channel channel = findChannelByTitle(title).orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        data.remove(channel.getId());
    }
}
