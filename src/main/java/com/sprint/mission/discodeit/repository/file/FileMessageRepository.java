package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.dat";

    @Override
    public Message save(Message message) {
        List<Message> messages = findAll();
        boolean found = false;

        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                messages.set(i, message);
                found = true;
                break;
            }
        }

        if (!found) messages.add(message);

        saveToFile(messages);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return findAll().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void deleteById(UUID id) {
        List<Message> messages = findAll();
        messages.removeIf(m -> m.getId().equals(id));
        saveToFile(messages);
    }

    private void saveToFile(List<Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException("메세지 파일 저장 실패", e);
        }
    }
}
