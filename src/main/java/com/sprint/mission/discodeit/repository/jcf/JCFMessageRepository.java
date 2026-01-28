package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
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
                .filter(message -> message.getMessageChannel().getId().equals(channelId))
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
    }
}
