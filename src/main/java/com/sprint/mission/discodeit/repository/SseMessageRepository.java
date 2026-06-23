package com.sprint.mission.discodeit.repository;

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
  private static final int MAX_CACHE_SIZE = 1000;

  public void save(SseMessage message) {
    messages.put(message.id(), message);
    eventIdQueue.add(message.id());

    while (eventIdQueue.size() > MAX_CACHE_SIZE) {
      UUID oldId = eventIdQueue.poll();
      if (oldId != null) {
        messages.remove(oldId);
      }
    }
  }

  public List<SseMessage> findUnsentMessages(UUID receiverId, UUID lastEventId) {
    List<SseMessage> unsent = new ArrayList<>();
    if (lastEventId == null) {
      return unsent;
    }

    boolean foundLastEvent = false;
    for (UUID id : eventIdQueue) {
      if (foundLastEvent) {
        SseMessage msg = messages.get(id);
        if (msg != null && (msg.receiverId() == null || msg.receiverId().equals(receiverId))) {
          unsent.add(msg);
        }
      } else if (id.equals(lastEventId)) {
        foundLastEvent = true;
      }
    }

    if (!foundLastEvent) {
      for (UUID id : eventIdQueue) {
        SseMessage msg = messages.get(id);
        if (msg != null && (msg.receiverId() == null || msg.receiverId().equals(receiverId))) {
          unsent.add(msg);
        }
      }
    }

    return unsent;
  }
}
