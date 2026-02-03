package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.*;

@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "jcf",
        matchIfMissing = true
)
@Repository
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository(){
        data = new HashMap<>();
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
    public void save(Message message) {
        data.put(message.getId(),message);
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }
}
