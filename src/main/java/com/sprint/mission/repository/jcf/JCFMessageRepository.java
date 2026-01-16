package com.sprint.mission.repository.jcf;

import com.sprint.mission.entity.Message;
import com.sprint.mission.repository.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

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
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data.values());
    }

    @Override
    public void deleteById(UUID MessageId) {
        data.remove(MessageId);
    }

    @Override
    public List<Message> findByChannelId(UUID channelId) {
        return data.values().stream()
                .filter(message ->
                        message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId) {
        return data.values().stream()
                .filter(message ->
                        message.getUser().getId().equals(userId) &&
                                message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }
}
