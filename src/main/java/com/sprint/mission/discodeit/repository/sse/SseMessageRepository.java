package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  public void save(String eventName, Object data) {
    // 이벤트 ID 생성
    UUID eventId = UUID.randomUUID();
    // 이벤트ID큐에 해당 ID 저장
    eventIdQueue.add(eventId);

    // 파라미터를 받아 SseMessage 객체 생성
    SseMessage sseMessage = new SseMessage(eventId, eventName, data);

    // 이벤트 ID 별 전송해야할 이벤트 정보(SseMessage)를 담기
    messages.put(eventId, sseMessage);
  }
  
}
