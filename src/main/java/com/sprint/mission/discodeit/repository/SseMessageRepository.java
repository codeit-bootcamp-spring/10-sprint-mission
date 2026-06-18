package com.sprint.mission.discodeit.repository;

import static java.util.Objects.requireNonNull;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {
  private static final int MAX_MESSAGE_SIZE = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();


  public SseMessage save(SseMessage message){
    requireNonNull(message, "message");
    requireNonNull(message.id(), "message.id");

    messages.put(message.id(), message);
    eventIdQueue.add(message.id());

    deleteOldestIfExceedLimit();

    return message;
  }

  public List<SseMessage> findAllAfter(UUID lastEventId) {
    requireNonNull(lastEventId, "lastEventId");

    List<SseMessage> result = new ArrayList<>();
    boolean foundLastEventId = false;

    for (UUID eventId : eventIdQueue) {
      if (foundLastEventId) {
        SseMessage message = messages.get(eventId);

        if (message != null) {
          result.add(message);

        }
        continue;
      }

      if (eventId.equals(lastEventId)) {
        foundLastEventId = true;
      }
    }

    return result;
  }

  private void deleteOldestIfExceedLimit() {
    while (eventIdQueue.size() > MAX_MESSAGE_SIZE) {
      UUID oldestEventId = eventIdQueue.pollFirst();

      if (oldestEventId == null) {
        return;
      }

      messages.remove(oldestEventId);
    }
  }
}
