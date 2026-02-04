package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type" ,
        havingValue = "jcf" ,
        matchIfMissing = true
)
public class JCFMessageRepository implements MessageRepository {
    private final List<Message> data;       // 모든 메시지

    public JCFMessageRepository() {
        data = new ArrayList<>();
    }

    // 메시지 저장
    @Override
    public void save(Message message) {
        data.removeIf(existMessage -> existMessage.getId().equals(message.getId()));

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

    @Override
    public Instant getLastMessageAt(UUID channelId) {
        return findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .max(Comparator.comparing(Message::getCreatedAt))
                .map(Message::getCreatedAt)
                .orElse(null);
    }

}