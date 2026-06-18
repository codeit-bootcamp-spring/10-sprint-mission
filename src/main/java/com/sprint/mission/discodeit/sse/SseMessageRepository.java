package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage message) {
    messages.put(message.getId(), message);
    eventIdQueue.addLast(message.getId());
  }

  public SseMessage findById(UUID eventId) {
    return messages.get(eventId);
  }

  public List<SseMessage> findAfter(UUID lastEventId) {
    List<SseMessage> result = new ArrayList<>();

    boolean found = false;

    for (UUID eventId : eventIdQueue) {
      if (!found) {
        if (eventId.equals(lastEventId)) {
          found = true;
        }
        continue;
      }

      SseMessage message = messages.get(eventId);

      if (message != null) {
        result.add(message);
      }
    }

    return result;
  }
}
