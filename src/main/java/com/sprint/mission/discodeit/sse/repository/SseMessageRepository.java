package com.sprint.mission.discodeit.sse.repository;

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

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage message) {
    if (messages.putIfAbsent(message.id(), message) == null) {
      eventIdQueue.add(message.id());
    }
  }

  public List<SseMessage> findMessagesAfter(UUID receiverId, UUID lastEventId) {
    if (lastEventId == null || receiverId == null) {
      return new ArrayList<>();
    }
    if (eventIdQueue.contains(lastEventId)) {
      boolean found = false;
      List<SseMessage> result = new ArrayList<>();
      for (UUID eventId : eventIdQueue) {
        if (found) {
          SseMessage message = messages.get(eventId);
          if (message != null && receiverId.equals(message.receiverId())) {
            result.add(message);
          }
        }
        if (eventId.equals(lastEventId)) {
          found = true;
        }
      }
      return result;
    }
    return new ArrayList<>();
  }

}
