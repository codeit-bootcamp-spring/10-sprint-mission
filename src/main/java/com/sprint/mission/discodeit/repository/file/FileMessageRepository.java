package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "message.dat";

    private List<Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveData(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Message message) {
        List<Message> data = loadData();
        Optional<Message> existingMessageOpt = data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst();

        if (existingMessageOpt.isPresent()) {
            Message existingMessage = existingMessageOpt.get();
            existingMessage.update(message.getContent());
        } else {
            data.add(message);
        }
        saveData(data);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        List<Message> data = loadData();
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return loadData();
    }

    @Override
    public void delete(UUID id) {
        List<Message> data = loadData();
        data.removeIf(message -> message.getId().equals(id));
        saveData(data);
    }

    @Override
    public void deleteAll() {
        saveData(new ArrayList<>());
    }
}
