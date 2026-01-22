package com.sprint.mission.descodeit.repository.file;

import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.repository.MessageRepository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private static final String MESSAGE_FILE = "data/message.ser";

    @Override
    public Message findById(UUID messageId) {
        Map<UUID,Message> data = loadData();
        return data.get(messageId);
    }

    @Override
    public List<Message> findAll() {
        Map<UUID, Message> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public void save(UUID messageId, Message message) {
        Map<UUID, Message> data = loadData();
        data.put(messageId, message);
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
