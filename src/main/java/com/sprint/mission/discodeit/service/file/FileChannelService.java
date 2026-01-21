package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.listener.ChannelLifecycleListener;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private Map<UUID, Channel> channelDB = new HashMap<>();
    private final File file = new File("channels.ser");

    private final List<ChannelLifecycleListener> listeners = new ArrayList<>();

    public FileChannelService() {
        if (file.exists()) {
            load();
        } else {
            persist();
        }
    }

    @SuppressWarnings("unckecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.channelDB = (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("채널 데이터 로드 중 오류: " + e.getMessage());
            this.channelDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.channelDB);
        } catch (IOException e) {
            System.err.println("채널 데이터 저장 중 오류: " + e.getMessage());
        }
    }

    public void addListener(ChannelLifecycleListener listener) {
        listeners.add(listener);
    }

    private void validateDuplicateChannelName(String channelName) {
        boolean isDuplicate = channelDB.values().stream()
                .anyMatch(ch -> ch.getChannelName().equals(channelName));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다. (channelName: " + channelName + " )");
        }
    }

    @Override
    public Channel createChannel(String name, User owner) {
        validateDuplicateChannelName(name);
        Channel channel = new Channel(name, owner);
        channelDB.put(channel.getId(), channel);
        persist();
        return channel;
    }

    @Override
    public Channel findChannelById(UUID channelId) {
        if (!channelDB.containsKey(channelId)) {
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
        validateDuplicateChannelName(newChannelName);
        channel.updateChannelName(newChannelName);
        persist();
        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        findChannelById(channelId);
        for (ChannelLifecycleListener listener : listeners) {
            listener.onChannelDelete(channelId);
        }

        channelDB.remove(channelId);
        persist();
        System.out.println("채널 삭제 왼료되었습니다. (File)");
    }
    @Override
    public void deleteChannelsByOwnerId(UUID ownerId) {
        List<UUID> targetIds = channelDB.values().stream()
                .filter(ch -> ch.getOwner().getId().equals(ownerId))
                .map(Channel::getId)
                .toList();

        for (UUID id : targetIds) {
            deleteChannel(id);
        }
        persist();
    }
}
