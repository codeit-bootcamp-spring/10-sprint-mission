package com.sprint.mission.discodeit.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
@Slf4j
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID receiverId, SseEmitter emitter) {
    data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    emitter.onCompletion(() -> {
      log.info("[SSE] 연결 완료 및 정리 (userId={})", receiverId);
      delete(receiverId, emitter);
    });
    emitter.onTimeout(() -> {
      log.warn("[SSE] 타임아웃 발생 (userId={})", receiverId);
      delete(receiverId, emitter);
    });
    emitter.onError((ex) -> {
      log.error("[SSE] 네트워크 오류 발생 (userId={}, error={})", receiverId, ex.getMessage());
      delete(receiverId, emitter);
    });
    return emitter;
  }

  public void delete(UUID receiverId, SseEmitter sseEmitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters != null) {
      emitters.remove(sseEmitter);
      if (emitters.isEmpty()) {
        data.remove(receiverId);
      }
    }
  }

  public List<SseEmitter> findByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, new CopyOnWriteArrayList<>());
  }

  public Map<UUID, List<SseEmitter>> findAll() {
    return Map.copyOf(data);
  }
}
