package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf",
        matchIfMissing = true
)
public class JcfMessageRepository implements MessageRepository {

    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    public void reset() {
        messages.clear();
    }

    @Override
    public synchronized Optional<Message> findById(UUID id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(messages.get(id));
    }

    @Override
    public synchronized List<Message> findAllByChannelId(UUID channelId) {
        if (channelId == null) return List.of();

        List<Message> result = new ArrayList<>();
        for (Message m : messages.values()) {
            if (channelId.equals(m.getChannelId())) result.add(m);
        }
        return result;
    }

    @Override
    public synchronized Message save(Message message) {
        if (message == null) throw new IllegalArgumentException("message is null");
        messages.put(message.getId(), message);
        return message;
    }

    @Override
    public synchronized void delete(UUID id) {
        if (id == null) return;
        if (messages.remove(id) == null) throw new MessageNotFoundException();
    }
}
