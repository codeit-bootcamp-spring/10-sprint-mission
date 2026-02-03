package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public Message save(Message message) {
        this.data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(this.data.get(messageId));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return this.data.values().stream()
                .filter(m -> Objects.equals(m.getChannelId(), channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID messageId) {
        this.data.remove(messageId);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        findAllByChannelId(channelId)
                .forEach(m -> deleteById(m.getId()));
    }
}
