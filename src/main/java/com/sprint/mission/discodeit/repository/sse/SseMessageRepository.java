package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Repository
public class SseMessageRepository {
    private static final int MAX_CACHE_SIZE = 1000;

    /// 큐: 선입선출
    private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
    private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

    /// 이벤트 유실 복원을 위해 SSE 메시지를 저장하는 컴포넌트
    public SseMessage save(UUID receiverId, String eventName, Object data) {
        UUID eventId = UUID.randomUUID();

        SseMessage message = new SseMessage(eventId,receiverId, eventName, data);
        /// 맨 뒤에 추가.
        eventIdQueue.addLast(eventId);
        messages.put(eventId, message);

        deleteOldMessages();

        return message;
    }

    /// 마지막 eventId기준 이후 메시지 조회
    public List<SseMessage> findAllAfter(UUID receiverId, UUID lastEventId) {
        List<SseMessage> result = new ArrayList<>();

        boolean foundLastEventId = false;

        for (UUID eventId : eventIdQueue) {
            if (!foundLastEventId) {
                if (eventId.equals(lastEventId)) {
                    foundLastEventId = true;
                }
                continue;
            }

            SseMessage message = messages.get(eventId);
            if (message != null && message.receiverId().equals(receiverId)) {
                result.add(message);
            }
        }

        return result;
    }

    /// 큐에 오래된 event 삭제.
    private void deleteOldMessages() {
        /// 1000개 초과시.
        while (eventIdQueue.size() > MAX_CACHE_SIZE) {
            /// 제일 오래된 eventId
            UUID oldEventId = eventIdQueue.pollFirst();

            if (oldEventId != null) {
                messages.remove(oldEventId);
            }
        }
    }


}
