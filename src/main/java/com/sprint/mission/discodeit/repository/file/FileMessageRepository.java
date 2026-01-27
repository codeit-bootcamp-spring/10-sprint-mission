package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class FileMessageRepository extends AbstractFileRepository<Message> implements MessageRepository {

    public FileMessageRepository() {
        super("Message.ser");
    }

    @Override
    public Message save(Message message) {
        Map<UUID, Message> data = load();
        data.put(message.getId(), message);
        writeToFile(data);
        return message;
    }

    public void saveAll(List<Message> messages) {
        Map<UUID, Message> data = messages.stream().collect(Collectors.toMap(msg -> msg.getId(), Function.identity()));

        writeToFile(data);
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
