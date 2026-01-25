package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data = new ArrayList<>();

    @Override
    public void save(Message message) {
        Optional<Message> existingMessageOpt = data.stream()
                .filter(m -> m.getId().equals(message.getId()))
                .findFirst();

        if (existingMessageOpt.isPresent()) {
            Message existingMessage = existingMessageOpt.get();
            // Assuming Message has an update method; adjust based on actual fields
            existingMessage.update(message.getContent()); // Example: update content or relevant fields
        } else {
            data.add(message);
        }
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
    }

    @Override
    public void deleteAll() {
        data.clear();
    }
}
