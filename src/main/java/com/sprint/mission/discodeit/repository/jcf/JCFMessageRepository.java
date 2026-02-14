package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> messageDB = new HashMap<>();

    // Create, Update
    @Override
    public Message save(Message message) {
        messageDB.put(message.getId(), message);
        return message;
    }

    // Read
    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(messageDB.get(id));
    }
    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        return messageDB.values().stream()
                .filter(msg -> msg.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .collect(Collectors.toList());
    }

    // Delete
    @Override
    public void delete(UUID id) {
        messageDB.remove(id);
    }
    @Override
    public void deleteAllByUserId(UUID userId) {
        messageDB.values().removeIf(msg -> msg.getSender().getId().equals(userId));
    }
    @Override
    public void deleteAllByChannelId(UUID channelId) {
        messageDB.values().removeIf(msg -> msg.getChannel().getId().equals(channelId));
    }
}