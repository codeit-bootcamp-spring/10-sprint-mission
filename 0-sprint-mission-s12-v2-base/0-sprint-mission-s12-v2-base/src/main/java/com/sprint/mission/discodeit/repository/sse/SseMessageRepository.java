package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.Collection;
import java.util.HashSet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public SseMessage save(Collection<UUID> receiverIds, String eventName, Object data) {
        return save(eventName, data, new HashSet<>(receiverIds), false);
    }

    public SseMessage save(String eventName, Object data) {
        return save(eventName, data, Set.of(), true);
    }

    private SseMessage save(String eventName, Object data, Set<UUID> receiverIds, boolean broadcast) {
        UUID eventId = UUID.randomUUID();
        SseMessage message = new SseMessage(eventId, eventName, data, receiverIds, broadcast);
        messages.put(eventId, message);
        eventIdQueue.addLast(eventId);
        evictOldMessages();
        return message;
    }

    public List<SseMessage> findAllAfter(UUID receiverId, UUID lastEventId) {
        if (lastEventId == null || !messages.containsKey(lastEventId)) {
            return List.of();
        }

        return eventIdQueue.stream()
                .dropWhile(eventId -> !eventId.equals(lastEventId))
                .skip(1)
                .map(messages::get)
                .filter(message -> message != null)
                .filter(message -> message.broadcast() || message.receiverIds().contains(receiverId))
                .toList();
    }

    private void evictOldMessages() {
        while (eventIdQueue.size() > 1000) {
            UUID eventId = eventIdQueue.pollFirst();
            if (eventId != null) {
                messages.remove(eventId);
            }
        }
    }
}
