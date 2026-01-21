package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final String FILE_PATH = "channels.dat";

    @SuppressWarnings("unchecked")
    private List<Channel> loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Channel channel) {
        List<Channel> channels = loadAll();
        channels.add(channel);
        saveAll(channels);
    }

    @Override
    public Channel findById(UUID id) {
        return loadAll().stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Channel> findAll() {
        return loadAll();
    }

    @Override
    public void update(Channel channel) {
        List<Channel> channels = loadAll();
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getId().equals(channel.getId())) {
                channels.set(i, channel);
                saveAll(channels);
                return;
            }
        }
    }

    @Override
    public void delete(UUID id) {
        List<Channel> channels = loadAll();
        channels.removeIf(c -> c.getId().equals(id));
        saveAll(channels);
    }
}