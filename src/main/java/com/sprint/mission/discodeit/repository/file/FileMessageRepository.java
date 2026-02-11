
package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@Primary
public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "messages.dat";
    private Map<UUID, Message> data;

    public FileMessageRepository() {
        this.data = loadFromFile();
    }

    @Override
    public void save(Message message) {
        if(message==null) throw new IllegalArgumentException("message가 null일 수 없습니다.");
        data.put(message.getId(), message);
        saveToFile();
    }

    @Override
    public void delete(UUID id) {
        if(id==null) throw new IllegalArgumentException("messageId가 null일 수 없습니다.");
        data.remove(id);
        saveToFile();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if(id==null) throw new IllegalArgumentException("messageId가 null일 수 없습니다.");
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }




    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("Message 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
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
