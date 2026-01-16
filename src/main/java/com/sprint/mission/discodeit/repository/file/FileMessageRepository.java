package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository(String path) {
        super(path);
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = load();
        data.put(message.getId(), message);
        writeToFile(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        Map<UUID, Message> data = load();
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> readAll() {
        Map<UUID, Message> data = load();
        return List.copyOf(data.values());
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = load();
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, Message>());
    }
}
