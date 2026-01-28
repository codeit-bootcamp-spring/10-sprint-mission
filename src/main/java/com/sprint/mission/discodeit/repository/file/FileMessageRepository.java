package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private final String FILE_PATH = "messages.dat";
    private final Map<UUID, Message> cache;

    public FileMessageRepository(){
        this.cache = loadData();
    }

    private Map<UUID, Message> loadData(){
        File file = new File(FILE_PATH);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, Message>) ois.readObject();
        } catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            oos.writeObject(cache);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void save(Message message) {
        cache.put(message.getId(), message);
        saveData();
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(cache.values());
    }

    @Override
    public void delete(UUID id) {
        cache.remove(id);
        saveData();
    }
}
