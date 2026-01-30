package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

@Repository
public class FileMessageRepository implements MessageRepository {
    private static final String MESSAGE_FILE = "data/message.ser";

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(loadData().get(messageId));
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(Message message) {
        Map<UUID, Message> data = loadData();
        data.put(message.getId(), message);
        saveData(data);
    }

    @Override
    public void delete(UUID messageId) {
        Map<UUID, Message> data = loadData();
        data.remove(messageId);
        saveData(data);
    }

    private Map<UUID, Message> loadData(){
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(MESSAGE_FILE))){
            return (Map<UUID,Message>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data){
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(MESSAGE_FILE)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
