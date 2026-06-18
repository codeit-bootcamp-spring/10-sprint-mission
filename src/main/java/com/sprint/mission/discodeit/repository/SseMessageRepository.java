package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

    private static final int MAX_SIZE = 1000;

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    public SseMessage save(SseMessage message) {
        messages.put(message.id(), message);
        eventIdQueue.addLast(message.id());
        evictIfOverflow();
        return message;
    }

    public List<SseMessage> findAllByReceiverIdAndIdAfter(UUID receiverId, UUID lastEventId) {
        if (lastEventId == null) {
            return List.of();
        }
        List<SseMessage> result = new ArrayList<>();
        boolean afterLast = false;
        for (UUID id : eventIdQueue) {
            if (afterLast) {
                SseMessage message = messages.get(id);
                if (message != null && message.receiverIds().contains(receiverId)) {
                    result.add(message);
                }
            } else if (id.equals(lastEventId)) {
                afterLast = true;
            }
        }
        return result;
    }

    private void evictIfOverflow() {
        while (messages.size() > MAX_SIZE) {
            UUID oldest = eventIdQueue.pollFirst();
            if (oldest == null) {
                return;
            }
            messages.remove(oldest);
        }
    }
}
