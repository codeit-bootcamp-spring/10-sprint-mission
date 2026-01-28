package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private Map<UUID, Message> messageData;

    private static final Path BASE_PATH = Path.of("data/message");
    private static final Path STORE_FILE = BASE_PATH.resolve("message.ser");

    public FileMessageRepository() {
        init();
        loadData();
    }

    // 디렉 체크
    private void init() {
        try {
            if (!Files.exists(BASE_PATH)) {
                Files.createDirectories(BASE_PATH);
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    // 저장 (직렬화)
    void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORE_FILE.toFile()))) {
            oos.writeObject(messageData);
        } catch (IOException e) {
            throw new RuntimeException("Data save failed." + e.getMessage());
        }
    }

    // 로드 (역직렬화)
    private void loadData() {
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(STORE_FILE)) {
            messageData = new HashMap<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORE_FILE.toFile()))){
            messageData = (Map<UUID, Message>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }
    @Override
    public Optional<Message> find(UUID messageID) {
        loadData();
        return messageData.values().stream()
                .filter(message -> message.getId().equals(messageID))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        loadData();
        return messageData.values().stream().toList();
    }

    @Override
    public void deleteMessage(UUID messageID) {
        loadData();
        messageData.remove(messageID);
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
