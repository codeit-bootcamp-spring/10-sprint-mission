package com.sprint.mission.discodeit.repository.sse;

import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {

    private static final int MAX_MESSAGE_COUNT = 1000;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public void save(SseMessage message) {
        messages.put(message.id(), message);
        eventIdQueue.addLast(message.id());
        trimToMaxSize();
    }

    public List<SseMessage> findAllAfter(UUID lastEventId, UUID receiverId) {
        if (lastEventId == null) {
            return List.of();
        }

        List<SseMessage> result = new ArrayList<>();
        boolean found = false;
        boolean collect = false;

        for (UUID eventId : eventIdQueue) {
            if (!collect) {
                if (eventId.equals(lastEventId)) {
                    found = true;
                    collect = true;
                }
                continue;
            }

            SseMessage message = messages.get(eventId);
            if (message != null && message.matches(receiverId)) {
                result.add(message);
            }
        }

        if (found) {
            return result;
        }

        return eventIdQueue.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .filter(message -> message.matches(receiverId))
                .toList();
    }

    private void trimToMaxSize() {
        while (eventIdQueue.size() > MAX_MESSAGE_COUNT) {
            UUID oldestEventId = eventIdQueue.pollFirst();
            if (oldestEventId != null) {
                messages.remove(oldestEventId);
            }
        }
    }
}