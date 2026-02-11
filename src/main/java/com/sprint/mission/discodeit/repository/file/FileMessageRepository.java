package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "messages.ser";
    private Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = load();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        return data.values().stream().toList();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getSentChannelId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public void deleteById(UUID messageId) {
        data.remove(messageId);
        saveToFile();
    }

    @Override
    public void deleteByChannelId(UUID channelId) {
        data.values().removeIf(
                message -> message.getSentChannelId().equals(channelId)
        );
        saveToFile();
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, Message> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 로드 실패", e);
        }
    }

    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
