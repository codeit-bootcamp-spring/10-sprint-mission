package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;
    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }
    @Override
    public Message save(Message message) {
        return data.put(message.getId(), message);
    }

    @Override
    public void saveAll() {
        //맵에 이미 객체 변경사항 반영
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAllByChannelIdOrderByCreatedAtAsc(UUID channelId) {
        return data.values().stream()
                .filter(m->m.getChannel().getId().equals(channelId))
                .sorted(Comparator
                        .comparing(Message::getCreatedAt)//시간순
                        .thenComparing(m->m.getSequence())//같은 시간이면 시퀀스로 정렬
                )
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        data.values().removeIf(m->m.getChannel().getId().equals(channelId));
    }
}
