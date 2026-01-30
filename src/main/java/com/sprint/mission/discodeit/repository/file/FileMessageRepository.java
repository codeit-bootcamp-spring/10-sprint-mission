package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FileMessageRepository implements MessageRepository {

    private static final String FILE_PATH = "messages.dat";

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> loadMessageFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof Map<?, ?>) return (Map<UUID, Message>) obj;
            return new LinkedHashMap<>();
        } catch (EOFException e) {
            return new LinkedHashMap<>();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveMessageFile(Map<UUID, Message> messages) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(messages);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 초기화(원하면 유지)
    public void resetMessageFile() {
        saveMessageFile(new LinkedHashMap<>());
    }

    @Override
    public synchronized Optional<Message> findById(UUID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(loadMessageFile().get(id));
    }

    @Override
    public synchronized List<Message> findAllByChannelId(UUID channelId) {
        if (channelId == null) return List.of();

        List<Message> result = new ArrayList<>();
        for (Message m : loadMessageFile().values()) {
            if (channelId.equals(m.getId())) result.add(m);
        }
        return result;
    }

    @Override
    public synchronized Message save(Message message) {
        if (message == null) throw new IllegalArgumentException("message is null");

        Map<UUID, Message> messages = loadMessageFile();
        messages.put(message.getId(), message);
        saveMessageFile(messages);

        return message;
    }

    @Override
    public synchronized void delete(UUID id) {
        if (id == null) return;

        Map<UUID, Message> messages = loadMessageFile();
        if (messages.remove(id) == null) throw new MessageNotFoundException();
        saveMessageFile(messages);
    }
}
