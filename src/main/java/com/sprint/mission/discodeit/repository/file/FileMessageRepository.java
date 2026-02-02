package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {
    private final File file;
    private final Map<UUID, Message> cache;

    public FileMessageRepository(@Value("${discodeit.repository.file-directory:.discodeit}") String directory) {
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, "messages.dat");
        this.cache = loadData();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadData() {
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
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
    public void save(Message message) {
        cache.put(message.getId(), message);
        saveData();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}