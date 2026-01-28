package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private final List<Message> data = new ArrayList<>();

    @Override
    public Message save(Message message) {
        delete(message);
        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메시지 아이디입니다."));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void delete(Message message) {
        data.removeIf(m -> m.equals(message));
    }
}
