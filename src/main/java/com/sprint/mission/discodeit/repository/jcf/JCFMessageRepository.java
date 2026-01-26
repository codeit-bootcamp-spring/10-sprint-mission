package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data = new ArrayList<>();
    @Override
    public Message save(Message message) {
        data.removeIf(m -> m.getId().equals(message.getId()));
        data.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(m -> m.getId().equals(id));
    }
}
