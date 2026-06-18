package com.sprint.mission.discodeit.sse;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final int MESSAGE_QUEUE_QUANTITY = 1000;

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(SseMessage message) {
    messages.put(message.getId(), message);
    eventIdQueue.addLast(message.getId());

    if (eventIdQueue.size() > MESSAGE_QUEUE_QUANTITY) {
      UUID oldId = eventIdQueue.pollFirst();
      messages.remove(oldId);
    }
  }

  public List<SseMessage> getMessagesAfterLastEventId(UUID receiverId, UUID lastEventId) {
    final boolean[] found = {false};

    return eventIdQueue.stream()
        .dropWhile(id -> {
          if (found[0]) {
            return false;
          }
          if (id.equals(lastEventId)) {
            found[0] = true;
            return true;
          }
          return true;
        })
        .map(messages::get)
        .filter(Objects::nonNull)
        .filter(msg -> msg.isBroadcast() || msg.getReceiverIds().contains(receiverId))
        .toList();
  }
}
