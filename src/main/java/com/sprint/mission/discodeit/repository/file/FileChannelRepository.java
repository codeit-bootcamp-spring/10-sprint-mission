package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channels.dat";

    private Map<UUID, Channel> loadChannelFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveChannelFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetChannelFile() {
        saveChannelFile(new LinkedHashMap<>());
    }

    @Override
    public Channel createChannel(Channel channel) {
        Map<UUID, Channel> channels = loadChannelFile();
        channels.put(channel.getId(), channel);
        saveChannelFile(channels);
        return channel;
    }

    @Override
    public Channel saveChannel(Channel channel) {
        Map<UUID, Channel> channels = loadChannelFile();
        channels.put(channel.getId(), channel);
        saveChannelFile(channels);
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        Channel channel = loadChannelFile().get(channelId);
        if (channel == null) throw new ChannelNotFoundException();
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(loadChannelFile().values());
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Map<UUID, Channel> channels = loadChannelFile();
        if (channels.remove(channelId) == null) throw new ChannelNotFoundException();
        saveChannelFile(channels);
    }
}
