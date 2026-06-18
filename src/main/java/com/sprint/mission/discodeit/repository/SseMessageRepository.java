package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private static final int MAX_EVENT_COUNT = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public SseMessage save(Collection<UUID> receiverIds, String eventName, Object data) {
    Set<UUID> distinctReceiverIds = receiverIds == null
        ? Set.of()
        : Set.copyOf(receiverIds);
    return save(new SseMessage(UUID.randomUUID(), distinctReceiverIds, eventName, data));
  }

  public SseMessage save(String eventName, Object data) {
    return save(new SseMessage(UUID.randomUUID(), Set.of(), eventName, data));
  }

  public List<SseMessage> findAllByEventIdAfter(UUID eventId, UUID receiverId) {
    boolean eventIdFound = false;
    List<UUID> eventIds = new LinkedHashSet<>(eventIdQueue).stream().toList();
    List<SseMessage> result = new ArrayList<>();

    for (UUID currentEventId : eventIds) {
      if (eventIdFound) {
        SseMessage message = messages.get(currentEventId);
        if (message != null && message.canReceive(receiverId)) {
          result.add(message);
        }
        continue;
      }

      if (currentEventId.equals(eventId)) {
        eventIdFound = true;
      }
    }

    return result;
  }

  private SseMessage save(SseMessage message) {
    eventIdQueue.addLast(message.id());
    messages.put(message.id(), message);
    deleteOldMessages();
    return message;
  }

  private void deleteOldMessages() {
    while (eventIdQueue.size() > MAX_EVENT_COUNT) {
      UUID eventId = eventIdQueue.pollFirst();
      if (eventId != null) {
        messages.remove(eventId);
      }
    }
  }
}
