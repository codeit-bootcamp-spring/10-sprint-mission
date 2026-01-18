package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;

import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.listener.ChannelLifecycleListener;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelDB = new HashMap<>();

    private final List<ChannelLifecycleListener> listeners = new ArrayList<>();
    public void addListener(ChannelLifecycleListener listener) {
        listeners.add(listener);
    }

    private void validateDuplicateName(String channelName) {
        Optional<Channel> duplicateChannel = channelDB.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst();
        if (duplicateChannel.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다. (channelName: " + channelName + " )");
        }
    }

    @Override
    public Channel createChannel(String name, User owner) {
        validateDuplicateName(name);

        Channel channel = new Channel(name, owner);
        channelDB.put(channel.getId(), channel);

        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        if(!channelDB.containsKey(channelId)) {
            throw new IllegalArgumentException("해당 id의 채널이 존재하지 않습니다. (channelId: " + channelId + " )");
        }
        return channelDB.get(channelId);
    }
    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(channelDB.values());
    }

    @Override
    public Channel updateChannel(UUID channelId, String newChannelName) {
        Channel channel = findChannelById(channelId);
        validateDuplicateName(newChannelName);
        channel.updateChannelName(newChannelName);
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelById(channelId);
        for (ChannelLifecycleListener listener : listeners) {
            listener.onChannelDelete(channelId);
        }

        channelDB.remove(channelId);
        System.out.println("\t[3] 채널 삭제 완료 (채널 삭제 준비 단계 1, 2 수행 완료 후 채널 삭제 완료)." +
                "\n\t\tchannelId: " + channelId);
    }
    @Override
    public void deleteChannelsByOwnerId(UUID ownerId) {
        List<UUID> targetChannelIds = channelDB.values().stream()
                .filter(ch -> ch.getOwner().getId().equals(ownerId))
                .map(Channel::getId)
                .toList();
        List<String> targetChannelNames = new ArrayList<>();

        for (UUID channelId : targetChannelIds) {
            targetChannelNames.add(findChannelById(channelId).getChannelName());
            deleteChannel(channelId);
        }

        System.out.println("[4] 방장(Owner) 탈퇴로 인한 채널 -> 전체 삭제 완료. 삭제된 채널 수: " + targetChannelIds.size());
        for (String channelName : targetChannelNames) {
            System.out.println("\tchannelName: " + channelName);
        }
    }
}