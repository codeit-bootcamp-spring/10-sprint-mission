package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.dat";

    @SuppressWarnings("unchecked")
    private List<Message> loadAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveAll(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Message message) {
        List<Message> messages = loadAll();
        messages.add(message);
        saveAll(messages);
    }

    @Override
    public Message findById(UUID id) {
        return loadAll().stream().filter(m -> m.getId().equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<Message> findAll() {
        return loadAll();
    }

    @Override
    public void update(Message message) {
        List<Message> messages = loadAll();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.set(i, message);
                saveAll(messages);
                return;
            }
        }
    }

    @Override
    public void delete(UUID id) {
        List<Message> messages = loadAll();
        messages.removeIf(m -> m.getId().equals(id));
        saveAll(messages);
    }
}