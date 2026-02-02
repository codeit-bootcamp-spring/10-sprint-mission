package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {
    private final File file;
    private final Map<UUID, Channel> cache;

    public FileChannelRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, "channels.dat");
        this.cache = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Channel> loadData() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(cache);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Channel channel) {
        cache.put(channel.getId(), channel);
        saveData();
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}