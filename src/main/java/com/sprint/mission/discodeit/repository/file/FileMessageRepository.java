package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

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
    public void delete(UUID messageId) {
        data.remove(messageId);
        fileObjectStore.saveData();
    }
}
