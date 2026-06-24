package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    SseMessage stored = message.withId(UUID.randomUUID());
    messages.put(stored.id(), stored);
    eventIdQueue.addLast(stored.id());
    evictOld();
    return stored;
  }

  public List<SseMessage> findAllAfter(UUID lastEventId) {
    List<UUID> ids = new ArrayList<>(eventIdQueue);
    int index = ids.indexOf(lastEventId);
    if (index < 0) {
      return List.of();
    }
    return ids.subList(index + 1, ids.size()).stream()
        .map(messages::get)
        .filter(Objects::nonNull)
        .toList();
  }

  public void evictOld() {
    while (eventIdQueue.size() > MAX_SIZE) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) {
        messages.remove(oldest);
      }
    }
  }
}
