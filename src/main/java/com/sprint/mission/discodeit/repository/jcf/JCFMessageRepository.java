package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "jcf", matchIfMissing = true)
public class JCFMessageRepository implements MessageRepository {

    private final List<Message> data = new ArrayList<>();

    @Override
    public Message save(Message message) {
        delete(message);
        data.add(message);
        return message;
    }

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
    public void delete(Message message) {
        data.removeIf(m -> m.equals(message));
    }
}
