package com.sprint.mission.discodeit.repository.sse;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.springframework.stereotype.Repository;

@Repository
public class SseMessageRepository {

  // 생성된 EventID를 순서대로 저장하는 큐
  private final ConcurrentLinkedDeque<UUID> eventIdQueue = new ConcurrentLinkedDeque<>();
  // <K-V> 가 <이벤트ID, SseMessage>인 SseMessage를 담는 map 객체
  private final Map<UUID, SseMessage> messages = new ConcurrentHashMap<>();

  // 수신자가 존재하는 이벤트
  public SseMessage save(String eventName, Object data, Collection<UUID> receiverIds) {
    return save(eventName, data, Set.copyOf(receiverIds), false);
  }

  // 수신자가 필요없는 브로드캐스트 SseMessage를 저장
  public SseMessage saveBroadcast(String eventName, Object data) {
    return save(eventName, data, Set.of(), true);
  }

  // eventId(마지막이벤트ID) 와 수신자ID를 통해 SseMessage 리스트를 반환
  public List<SseMessage> findAllAfter(UUID lastEventId, UUID receiverId) {
    boolean foundLastEvent = lastEventId == null; // 마지막 이벤트를 찾거나 lastEventID가 null이 아닌 이상 false
    List<SseMessage> result = new ArrayList<>();

    for (UUID eventId : eventIdQueue) {
      if (!foundLastEvent) { // 마지막 이벤트를 찾지 못했으면
        foundLastEvent = eventId.equals(lastEventId);
        // eventIdQueue 내에서 가리키고 있는 element(eventID)를
        // lastEventId와 대조해보고 boolean 값을 foundLastEvent에 대입
        continue;
      }

      // SseMessage를 가져옴.
      SseMessage message = messages.get(eventId);
      if (message == null) {
        continue;
      }

      // 메시지의 전파 범위가 broadcast거나 수신자 목록에 파라미터로 전달받은 수신자 ID가 존재한다면
      // SseMessage 리스트에 해당 요소를 추가함.
      if (message.broadcast() || message.receiverIds().contains(receiverId)) {
        result.add(message);
      }
    }

    return result;
  }

  // 파라미터들을 조합하여 SseMessage 조립 후 저장
  private SseMessage save(String eventName, Object data, Set<UUID> receiverIds,
      boolean broadcast) {
    UUID eventId = UUID.randomUUID(); // 고유의 eventID 부여
    eventIdQueue.add(eventId); // 큐에 해당 ID 추가

    // SseMessage 생성
    SseMessage sseMessage = new SseMessage(eventId, eventName, data, receiverIds, broadcast);
    // SseMessage를 담아놓는 map에 put
    messages.put(eventId, sseMessage);

    // 생성되고 저장된 SseMessage 객체 반환
    return sseMessage;
  }
}
