package com.sprint.mission.discodeit.sse;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {
  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  // 저장
  public void save(UUID receiverId, SseEmitter emitter) {
    data.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>()).add(emitter);
  }

  // 조회
  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public Map<UUID, List<SseEmitter>> findAll() {
    return data;
  }

  // 삭제
  public void remove(UUID receiverId, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters == null) {
      return;
    }
    emitters.remove(emitter);
    if (emitters.isEmpty()) {
      data.remove(receiverId);
    }
  }

}
