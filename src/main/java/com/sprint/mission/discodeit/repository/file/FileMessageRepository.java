package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.dat";
    private final Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = loadFromFile();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
        saveToFile();
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new IllegalStateException("messages.dat 저장에 실패했습니다.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            return (obj instanceof Map) ? (Map<UUID, Message>) obj : new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }
}