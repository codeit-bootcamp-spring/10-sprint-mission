package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public Channel createChannel(String name, String description) {
        Channel channel = new Channel(name, description);
        data.add(channel);
        save();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = findById(id);

        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateChannelDescription);

        channel.touch();
        save();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);
        data.remove(channel);
        save();
    }
}
