package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.time.Instant;
import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final HashMap<UUID, Message> data = new HashMap<>();

    @Override
    public void save(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void delete(Message message) {
        data.remove(message.getId());
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .toList();
    }

    @Override
    public Instant findLastMessageAtByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo)
                .orElse(null);
    }

    @Override
    public Map<UUID, Instant> findLastMessageAtByChannelIds(List<UUID> channelIds) {
        return data.values().stream()
                .filter(message -> channelIds.contains(message.getChannelId()))
                .collect(
                        java.util.stream.Collectors.toMap(
                                Message::getChannelId,
                                Message::getCreatedAt,
                                (t1, t2) -> t1.isAfter(t2) ? t1 : t2
                        )
                );
    }

    @Override
    public void deleteById(UUID messageId) {
        data.remove(messageId);
    }
}
