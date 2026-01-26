package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "Channel.ser";

    private List<Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private void saveData(List<Channel> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) { e.printStackTrace(); }
    }
    @Override
    public Channel save(Channel channel) {
        List<Channel> data = loadData();
        Optional<Channel> existingChannels = data.stream()
                .filter(c -> c.getId().equals(channel.getId()))
                .findFirst();

        if (existingChannels.isPresent()) {
            Channel existingChannel = existingChannels.get();
            existingChannel.update(channel.getName());
        } else {
            data.add(channel);
        }
        saveData(data);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return loadData().stream()
                .filter(c -> c.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return loadData();
    }

    @Override
    public void delete(UUID id) {
        List<Channel> data = loadData();
        data.removeIf(c -> c.getId().equals(id));
        saveData(data);
    }
}
