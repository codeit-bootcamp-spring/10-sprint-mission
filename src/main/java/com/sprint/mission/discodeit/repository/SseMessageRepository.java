package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.sse.SseMessage;
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

    public void save(SseMessage message) {
        messages.put(message.id(), message);
        eventIdQueue.addLast(message.id());
        while (eventIdQueue.size() > MAX_SIZE) {
            UUID old = eventIdQueue.pollFirst();
            if (old != null) {
                messages.remove(old);
            }
        }
    }

    public List<SseMessage> findMissedEvents(UUID receiverId, UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();
        boolean after = false;
        for (UUID id : eventIdQueue) {
            if (after) {
                SseMessage m = messages.get(id);
                if (m != null && (m.receiverIds() == null || m.receiverIds().contains(receiverId))) {
                    result.add(m);
                }
            }
            if (id.equals(lastEventId)) {
                after = true;
            }
        }
        return result;
    }
}
