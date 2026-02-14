package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> messages = new ArrayList<>();

    @Override
    public Message save(Message message) {
        int index = -1;
        for (int i = 0; i <  messages.size(); i++) {
            if (messages.get(i).getId().equals(message.getId())) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            messages.set(index, message);
        } else {
            messages.add(message);
        }
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return messages.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages);
    }

    @Override
    public void deleteById(UUID id) {
        messages.removeIf(m -> m.getId().equals(id));
    }
}
