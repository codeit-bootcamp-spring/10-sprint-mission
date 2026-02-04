package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileChannelRepository implements ChannelRepository {

    private final Path filePath;

    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String fileDirectory
    ) {
        try {
            Files.createDirectories(Paths.get(fileDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.filePath = Paths.get(fileDirectory, "channels.dat");
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadChannelFile() {
        File file = filePath.toFile();
        if (!file.exists()) return new LinkedHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveChannelFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetFile() {
        saveChannelFile(new LinkedHashMap<>());
    }

    @Override
    public UUID createChannel(Channel channel) {
        Map<UUID, Channel> channels = loadChannelFile();
        channels.put(channel.getId(), channel);
        saveChannelFile(channels);
        return channel.getId();
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
