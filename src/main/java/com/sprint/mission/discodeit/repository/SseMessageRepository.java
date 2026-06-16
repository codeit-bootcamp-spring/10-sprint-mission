package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final int MAX_CACHE_SIZE = 100;
  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  @Getter
  @AllArgsConstructor
  public static class SseMessage {

    private final String eventId;
    private final String eventName;
    private final Object data;
  }

  public void save(UUID eventId, SseMessage message) {
    messages.put(eventId, message);
    eventIdQueue.add(eventId);

    if (eventIdQueue.size() > MAX_CACHE_SIZE) {
      UUID oldestEventId = eventIdQueue.poll();
      if (oldestEventId != null) {
        messages.remove(oldestEventId);
      }
    }
  }

  public List<SseMessage> findAllAfter(UUID lastEventId) {
    List<SseMessage> missingMessages = new ArrayList<>();
    boolean found = false;
    for (UUID eventId : eventIdQueue) {
      if (found) {
        SseMessage message = messages.get(eventId);
        if (message != null) {
          missingMessages.add(message);
        }
      } else if (eventId.equals(lastEventId)) {
        found = true;
      }
    }
    return missingMessages;
  }
}
