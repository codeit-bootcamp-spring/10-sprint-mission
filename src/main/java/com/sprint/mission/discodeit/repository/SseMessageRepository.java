package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.sse.SseMessage;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final Duration RETENTION = Duration.ofMinutes(30);

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public SseMessage save(UUID receiverId, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();
        SseMessage message = new SseMessage(eventId, receiverId, eventName, data, Instant.now());

        eventIdQueue.add(eventId);
        messages.put(eventId, message);

        deleteExpiredMessages();

        return message;
    }

    public List<SseMessage> findAllByReceiverIdAfter(UUID receiverId, UUID lastEventId) {
        if (lastEventId == null) {
            return List.of();
        }

        boolean foundLastEvent = false;
        List<SseMessage> result = new ArrayList<>();

        for (UUID eventId : eventIdQueue) {
            if (eventId.equals(lastEventId)) {
                foundLastEvent = true;
                continue;
            }

            if (foundLastEvent) {
                SseMessage message = messages.get(eventId);

                if (message != null && message.receiverId().equals(receiverId)) {
                    result.add(message);
                }
            }
        }

        return result;
    }

    public void deleteExpiredMessages() {
        Instant threshold = Instant.now().minus(RETENTION);

        while (!eventIdQueue.isEmpty()) {
            UUID eventId = eventIdQueue.peekFirst();
            SseMessage message = messages.get(eventId);

            if (message == null) {
                eventIdQueue.pollFirst();
                continue;
            }

            if (message.createdAt().isAfter(threshold)) {
                break;
            }

            eventIdQueue.pollFirst();
            messages.remove(eventId);
        }
    }
}

