package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final String FILE_PATH = "channels.dat";

    @SuppressWarnings("unchecked")
    private List<Channel> loadChannels() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveChannels(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel create(String name) {
        List<Channel> channels = loadChannels();
        Channel newChannel = new Channel(name);
        channels.add(newChannel);
        saveChannels(channels);
        return newChannel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return loadChannels().stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return loadChannels();
    }

    @Override
    public Channel update(UUID channelId, String name) {
        List<Channel> channels = loadChannels();
        for (Channel channel : channels) {
            if (channel.getId().equals(channelId)) {
                channel.updateName(name);
                saveChannels(channels);
                return channel;
            }
        }
        return null;
    }

    @Override
    public void delete(UUID channelId) {
        List<Channel> channels = loadChannels();
        channels.removeIf(c -> c.getId().equals(channelId));
        saveChannels(channels);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        List<Channel> channels = loadChannels();
        for (Channel channel : channels) {
            if (channel.getId().equals(channelId)) {
                saveChannels(channels);
                return;
            }
        }
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        List<Channel> channels = loadChannels();
        for (Channel channel : channels) {
            if (channel.getId().equals(channelId)) {
                saveChannels(channels);
                return;
            }
        }
    }
}