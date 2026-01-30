package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;
    private final FileObjectStore fileObjectStore;

    public FileMessageRepository(FileObjectStore fileObjectStore) {
        this.data = fileObjectStore.getMessagesData();
        this.fileObjectStore = fileObjectStore;
    }

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
        fileObjectStore.saveData();
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public List<Message> findByAuthorId(UUID authorId) {
        return data.values().stream()
                .filter(message -> message.getAuthor().getId().equals(authorId))
                .toList();
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
        fileObjectStore.saveData();
    }
}
