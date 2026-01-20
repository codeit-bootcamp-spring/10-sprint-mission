package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final File file = new File("messages.dat");
    private Map<UUID, Message> messageMap;

    public FileMessageRepository(){
        if (file.exists()) {
            load();
        } else {
            this.messageMap = new HashMap<>();
        }
    }

    @SuppressWarnings("unchecked")
    public void load(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.messageMap = (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("데이터 로드 중 오류 발생" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.messageMap);
        } catch (IOException e) {
            System.out.println("파일 저장 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Message save(Message message) {
        messageMap.put(message.getId(), message);
        saveFile(); // 변경 사항 파일 반영
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return messageMap.get(id);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageMap.values());
    }

    @Override
    public void delete(UUID id) {
        messageMap.remove(id);
        saveFile();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageMap.values().stream()
                         .filter(message -> message.getChannel().getId().equals(channelId))
                         .toList();
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return messageMap.values().stream()
                         .filter(message -> message.getUser().getId().equals(userId))
                         .toList();
    }


}
