package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private Map<UUID, Message> messageData;

    private final Path basePath = Path.of("data/message");
    private final Path storeFile = basePath.resolve("message.ser");

    public FileMessageRepository() {
        init();
        loadData();
    }

    // 디렉 체크
    private void init() {
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    // 저장 (직렬화)
    void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))) {
            oos.writeObject(messageData);
        } catch (IOException e) {
            throw new RuntimeException("Data save failed." + e.getMessage());
        }
    }

    // 로드 (역직렬화)
    private void loadData() {
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(storeFile)) {
            messageData = new HashMap<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            messageData = (Map<UUID, Message>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }
    @Override
    public Message find(UUID messageID) {
        loadData();
        return messageData.values().stream()
                .filter(message -> message.getId().equals(messageID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Message Not Found: " + messageID));
    }

    @Override
    public List<Message> findAll() {
        loadData();
        return messageData.values().stream().toList();
    }

    @Override
    public void deleteMessage(Message message) {
        loadData();
        messageData.remove(message.getId());
        saveData();
    }

    @Override
    public Message save(Message message){
        loadData();
        messageData.put(message.getId(), message);
        saveData();
        return message;
    }
}
