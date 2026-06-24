package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.dto.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {

    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();
    private static final int MAX_MESSAGE_SIZE = 1000;

    public SseMessage save(Collection<UUID> receiverIds, String eventName, Object data) {
        UUID id = UUID.randomUUID();
        SseMessage message = new SseMessage(id, receiverIds, eventName, data);

        messages.put(id, message);
        eventIdQueue.addLast(id);

        if (eventIdQueue.size() > MAX_MESSAGE_SIZE) {
            UUID oldestId = eventIdQueue.removeFirst();
            messages.remove(oldestId);
        }

        return message;
    }

    public List<SseMessage> findAllByReceiverIdAndAfterId(UUID receiverId, UUID lastEventId) {
        boolean foundLastEvent = false;
        List<SseMessage> result = new ArrayList<>();

        for (UUID id : eventIdQueue) {
            if (foundLastEvent) {
                SseMessage msg = messages.get(id);
                if (msg != null) {
                    // 수신 대상자가 비어있으면(전체 방송) 혹은 이 사용자가 포함되어 있으면 추가
                    if (msg.receiverIds().isEmpty() || msg.receiverIds().contains(receiverId)) {
                        result.add(msg);
                    }
                }
            }

            if (id.equals(lastEventId)) {
                foundLastEvent = true;
            }
        }

        return result;
    }

}

