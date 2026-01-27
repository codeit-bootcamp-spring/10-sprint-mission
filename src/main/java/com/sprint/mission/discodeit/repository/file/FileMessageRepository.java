package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FileMessageRepository extends BaseFileRepository<Message> implements MessageRepository {
    public FileMessageRepository() {
        super("messages.ser");
    }

    @Override
    public Message save(Message message){
        Map<UUID, Message> data = loadData();
        data.put(message.getId(), message);
        saveData(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id){
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public List<Message> findAll(){
        return new ArrayList<>(loadData().values());
    }

    @Override
    public void delete(Message message){
        Map<UUID, Message> data = loadData();
        data.remove(message.getId());
        saveData(data);
    }
}
