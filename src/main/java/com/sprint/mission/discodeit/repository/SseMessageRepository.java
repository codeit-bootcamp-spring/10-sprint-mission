package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.Collection;
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

  public void save(UUID eventId, SseMessage message) {
    eventIdQueue.add(eventId);
    messages.put(eventId, message);

    //메모리 무한 확장 방지
    if (eventIdQueue.size() > 100) {
      UUID old = eventIdQueue.poll();
      messages.remove(old);
    }

  }

  public List<SseMessage> findAllByReceiverIdAfterEventId(UUID receiverId, UUID eventId) {
    List<SseMessage> result = new ArrayList<>();

    boolean found = false;

    for (UUID id : eventIdQueue) {
      if (found) {
        SseMessage message = messages.get(id);
        if (message != null && !message.eventName.equals("connected")
            && message.receiverIds.contains(receiverId)) {
          result.add(message);
        }
      }
      if (id.equals(eventId)) {
        found = true;
      }
    }
    return result;
  }

  public record SseMessage(UUID eventId, Collection<UUID> receiverIds, String eventName,
                           Object data) {

  }

}
