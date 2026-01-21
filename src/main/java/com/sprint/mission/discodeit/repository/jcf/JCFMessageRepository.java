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
    public Optional<Message> findById(UUID messageId) {
        return data.stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }

    @Override
    public List<Message> findAllByUserId(UUID userId) {
        return data.stream()
                .filter(m -> m.getSender().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return data.stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public void save(Message message) {
        data.add(message);
    }

    @Override
    public void deleteById(UUID messageId) {
        data.removeIf(m -> m.getId().equals(messageId));
    }
}
