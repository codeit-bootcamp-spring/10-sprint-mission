package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "Message.ser";

    private List<Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }

    private void saveData(List<Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public Message save(Message message) {
        List<Message> data = loadData();
        Optional<Message> existingMessages = data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst();

        if (existingMessages.isPresent()) {
            Message existingMessage = existingMessages.get();
            existingMessage.update(message.getText());
        } else {
            data.add(message);
        }
        saveData(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return loadData().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return loadData();
    }

    @Override
    public void delete(UUID id) {
        List<Message> data = loadData();
        data.removeIf(m -> m.getId().equals(id));
        saveData(data);
    }
}
