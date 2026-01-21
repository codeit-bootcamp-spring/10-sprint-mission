package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.dat";

    private Map<UUID, Message> loadData(){
        File file = new File(FILE_PATH);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, Message>) ois.readObject();
        } catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(data);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void save(Message message) {
        Map<UUID, Message> data = loadData();
        data.put(message.getId(), message);
        saveData(data);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = loadData();
        data.remove(id);
        saveData(data);
    }
}
