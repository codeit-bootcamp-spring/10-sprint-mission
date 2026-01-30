package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FileChannelService implements ChannelService {

    private final List<Channel> data = new ArrayList<>();
    private final Path filePath;

    public FileChannelService() {
        this.filePath = Path.of("data", "channels.ser");
        load();
    }

    private void load() {
        if (Files.notExists(filePath)) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
            @SuppressWarnings("unchecked")
            List<Channel> loaded = (List<Channel>) ois.readObject();
            data.clear();
            data.addAll(loaded);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
                oos.writeObject(data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 기존: createChannel(String name, String description)
    @Override
    public Channel create(ChannelType type, String name, String description) {
        // Channel 생성자가 (ChannelType, name, description)을 받는지 확인 필요
        // 만약 없다면 Channel 쪽에 생성자를 추가하거나 아래를 수정해야 함.
        Channel channel = new Channel(type, name, description);
        data.add(channel);
        save();
        return channel;
    }

    // 기존: findById(UUID id)
    @Override
    public Channel find(UUID channelId) {
        return data.stream()
                .filter(c -> c.getId().equals(channelId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    // 기존: updateChannel(UUID id, String name, String description) (void)
    @Override
    public Channel update(UUID channelId, String newName, String newDescription) {
        Channel channel = find(channelId);

        Optional.ofNullable(newName).ifPresent(channel::updateChannelName);
        Optional.ofNullable(newDescription).ifPresent(channel::updateChannelDescription);

        channel.touch();
        save();
        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = find(channelId);
        data.remove(channel);
        save();
    }
}
