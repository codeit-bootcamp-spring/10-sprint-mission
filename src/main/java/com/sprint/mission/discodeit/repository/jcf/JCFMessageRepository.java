package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;

    public JCFMessageRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID messageId) {
        return Optional.ofNullable(data.get(messageId));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .toList();
    }

    @Override
    public Optional<Instant> findLatestMessageTimeByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message -> channelId.equals(message.getChannelId()))
                .map(Message::getCreatedAt)
                .max(Instant::compareTo);
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID messageId) {
        data.remove(messageId);
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        List<UUID> messageIdsToDelete = data.values().stream()
                        .filter(message -> channelId.equals(message.getChannelId()))
                        .map(Message::getId)
                        .toList();

        for (UUID messageId : messageIdsToDelete) {
            data.remove(messageId);
        }
    }
}
