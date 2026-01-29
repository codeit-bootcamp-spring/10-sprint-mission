/*
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.utils.Validation;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE_PATH = "messages.dat";
    private Map<UUID, Message> data;

    public FileMessageService() {
        this.data = loadFromFile();
    }

    @Override
    public Message createMessage(String content, UUID senderId, UUID channelId) {
        Validation.notBlank(content, "메세지 내용");
        if (senderId == null || channelId == null) {
            throw new IllegalArgumentException("senderId나 channelId가 null일 수 없습니다.");
        }

        Message message = new Message(content, senderId, channelId);
        data.put(message.getId(), message);
        saveToFile();
        return message;
    }

    @Override
    public List<Message> getMessageAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message getMessageById(UUID id) {
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("해당 ID의 메세지가 존재하지 않습니다: " + id);
        }
        return message;
    }
    @Override
    public List<Message> getMsgListSenderId(UUID senderId) {
        return data.values().stream()
                .filter(m -> m.getSender() != null
                        && m.getSender().getId().equals(senderId))
                .toList();
    }

    @Override
    public Message updateMessage(UUID uuid, String newContent){
        Message existing = getMessageById(uuid);
        existing.update(newContent);
        saveToFile();
        return existing;
    }

    @Override
    public void deleteMessage(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제할 메세지가 존재하지 않습니다: " + id);
        }
        data.remove(id);
        saveToFile();
    }


    // 파일 저장
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("메세지 저장 중 오류 발생", e);
        }
    }

    // 파일 로드
    private Map<UUID, Message> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}

 */
