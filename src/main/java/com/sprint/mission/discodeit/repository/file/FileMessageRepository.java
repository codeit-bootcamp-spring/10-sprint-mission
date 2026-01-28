package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final File file = new File("messages.dat");
    private final Map<UUID, Message> messageStore;

    public FileMessageRepository() {
        this.messageStore = new HashMap<>();
        loadFile();
    }

    private void loadFile() {
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<UUID, Message> loaded = (Map<UUID, Message>) ois.readObject();
            messageStore.clear();
            messageStore.putAll(loaded);
        } catch (Exception e) {
            throw new RuntimeException("메세지 파일 로드 실패",e);
        }
    }

    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(messageStore);
        } catch (IOException e) {
            throw new RuntimeException("메세지 파일 저장 실패",e);
        }
    }

    @Override
    public Message save (Message message){
        messageStore.put(message.getId(),message);
        saveFile();
        return message;
    }

    @Override
    public Message findById (UUID id){
        Message message = messageStore.get(id);
        if(message == null){
            throw new IllegalArgumentException("해당 메세지를 찾을 수 없습니다");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messageStore.values());
    }

    @Override
    public void deleteById(UUID messageId) {
        messageStore.remove(messageId);
        saveFile();
    }

    @Override
    public List<Message> findByUserId(UUID userId) {
        return messageStore.values().stream()
                .filter(message -> message.getUserId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return messageStore.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }
}
