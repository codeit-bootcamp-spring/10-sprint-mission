package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file",
        matchIfMissing = false
)
public class FileMessageRepository implements MessageRepository {
    private final String filePath;
    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String directory){
        this.filePath = directory + "/message.ser";
    }

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
        File file = new File(filePath);
        if(!file.exists()) return new HashMap<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID,Message>) ois.readObject();
        }
        catch(Exception e){
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, Message> data){
        File file = new File(filePath);
        if(file.getParentFile() != null && !file.getParentFile().exists()){
            file.getParentFile().mkdirs();
        }
        try(ObjectOutputStream oos = new ObjectOutputStream((new FileOutputStream(file)))){
            oos.writeObject(data);
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
