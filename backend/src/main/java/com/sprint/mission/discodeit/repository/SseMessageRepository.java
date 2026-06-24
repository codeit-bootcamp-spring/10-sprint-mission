package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  // 최대 보관 메시지 수
  private static final int MAX_SIZE = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(SseMessage message) {
    messages.put(message.getId(), message);
    eventIdQueue.addLast(message.getId());
    evictIfNeeded();
    return message;
  }

  /**
   * lastEventId 이후에 저장된 메시지들을 순서대로 반환합니다.
   * lastEventId가 null이거나 존재하지 않으면 빈 리스트를 반환합니다.
   */
  public List<SseMessage> findAllAfter(UUID lastEventId) {
    if (lastEventId == null) {
      return List.of();
    }

    List<SseMessage> result = new ArrayList<>();
    boolean found = false;

    for (UUID id : eventIdQueue) {
      if (found) {
        SseMessage message = messages.get(id);
        if (message != null) {
          result.add(message);
        }
      } else if (id.equals(lastEventId)) {
        found = true;
      }
    }

    return result;
  }

  public void delete(UUID eventId) {
    messages.remove(eventId);
    eventIdQueue.remove(eventId);
  }

  private void evictIfNeeded() {
    while (eventIdQueue.size() > MAX_SIZE) {
      UUID oldest = eventIdQueue.pollFirst();
      if (oldest != null) {
        messages.remove(oldest);
      }
    }
  }
}
