package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;       // 모든 메시지

    public JCFMessageRepository() {
        data = new ArrayList<>();
    }

    // 메시지 저장
    @Override
    public void save(Message message) {
        data.add(message);
    }

    // 메시지 단건 조회
    @Override
    public Optional<Message> findById(UUID messageId) {
        return data.stream()
                .filter(message -> message.getId().equals(messageId))
                .findFirst();
    }

    // 메시지 전체 조회
    @Override
    public List<Message> findAll() {
        return data;
    }

    // 메시지 삭제
    @Override
    public void delete(Message message) {
        data.remove(message);
    }
}