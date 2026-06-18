package com.sprint.mission.discodeit.sse;

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
  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  private static final int MAX_SIZE = 1000; // 임의깂

  // 저장
  public void save(SseMessage message) {
    eventIdQueue.addLast(message.id());
    messages.put(message.id(), message);

    while (eventIdQueue.size() > MAX_SIZE) {
      UUID oldestId = eventIdQueue.pollFirst();
      if (oldestId != null) {
        messages.remove(oldestId);
      }
    }
  }

  // 유실 메시지 꺼내서 재전송
  public List<SseMessage> findAllAfter(UUID lastEventId) {
    List<UUID> ids = new ArrayList<>(eventIdQueue);
    int index = ids.indexOf(lastEventId);

    if (index == -1) {
      return List.of();
    }

    return ids.subList(index + 1, ids.size()).stream()
        .map(messages::get)
        .filter(Objects::nonNull)
        .toList();
  }

}
