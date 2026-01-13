package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelPermission;
import com.sprint.mission.discodeit.entity.PermissionTarget;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> channelMap = new HashMap<>();

    // Create
    @Override
    public Channel createChannel(String name, boolean isPublic) {
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
        channel.updateName(name);
    }

    @Override
    public void updateChannelVisibility(UUID channelId, boolean isPublic) {
        Channel channel = getChannelOrThrow(channelId);
        channel.updatePublic(isPublic);
    }

    // Permission Management
    @Override
    public void grantPermission(UUID channelId, UUID targetId, PermissionTarget type) {
        Channel channel = getChannelOrThrow(channelId);
        channel.addPermission(targetId, type);
    }

    @Override
    public void revokePermission(UUID channelId, UUID targetId) {
        Channel channel = getChannelOrThrow(channelId);
        channel.removePermission(targetId);
    }

    @Override
    public void removePermissionsByTargetId(UUID targetId) {   // 서버에서 역할이 삭제될 때 서버 내 모든 채널의 부여된 권한 제거
        channelMap.values().forEach(channel -> channel.removePermission(targetId));
    }

    // Delete
    @Override
    public void deleteChannel(UUID channelId) {
        if (!channelMap.containsKey(channelId)) {
            throw new NoSuchElementException("존재하지 않는 채널입니다. ID: " + channelId);
        }
        channelMap.remove(channelId);
    }

    // Helper
    private Channel getChannelOrThrow(UUID channelId) {
        return findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 채널입니다. ID: " + channelId));
    }
}