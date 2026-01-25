package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.config.FileStorageConfig;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageRepository implements MessageRepository {
    private Map<UUID, Message> messageDB = new HashMap<>();
    private final File file;

    public FileMessageRepository() {
        File dataDir = FileStorageConfig.getDataDirectory();
        this.file = new File(dataDir, "messages.ser");
        if (file.exists()) load(); else persist();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.messageDB = (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Message 데이터 로드 실패: " + e.getMessage());
            this.messageDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.messageDB);
        } catch (IOException e) {
            System.err.println("Message 데이터 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public Message save(Message message) {
        messageDB.put(message.getId(), message);
        persist();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageDB.get(id));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageDB.values().stream()
                .filter(msg -> msg.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        messageDB.remove(id);
        persist();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        boolean changed = messageDB.values().removeIf(msg -> msg.getSender().getId().equals(userId));
        if(changed) persist();
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        boolean changed = messageDB.values().removeIf(msg -> msg.getChannel().getId().equals(channelId));
        if(changed) persist();
    }
}