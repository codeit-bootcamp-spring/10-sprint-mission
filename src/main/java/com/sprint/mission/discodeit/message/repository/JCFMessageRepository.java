package com.sprint.mission.discodeit.message.repository;

import com.sprint.mission.discodeit.message.Message;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data = new ArrayList<>();

    @Override
    public Optional<Message> findById(UUID messageId) {
        return data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(m -> m.getSenderId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.stream()
                .filter(m -> m.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public void save(Message message) {
        if(data.contains(message))
            data.set(data.indexOf(message), message);
        else
            data.add(message);
    }

    @Override
    public void deleteById(UUID messageId) {
        data.removeIf(m -> m.getId().equals(messageId));
    }
}
