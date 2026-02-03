package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
@Repository
public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("message는 null일 수 없습니다.");
        }
        if (message.getId() == null) {
            throw new IllegalArgumentException("message.id는 null일 수 없습니다.");
        }

        data.put(message.getId(), message);
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        data.remove(id);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("id는 null일 수 없습니다.");
        }
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }
}
