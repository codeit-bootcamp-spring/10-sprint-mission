package com.sprint.mission.discodeit.sse.repository;

import java.util.HashMap;
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

  public void save(UUID userId, SseEmitter emitter) {
    data.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>()).add(emitter);
  }

  public List<SseEmitter> get(UUID userId) {
    return data.getOrDefault(userId, new CopyOnWriteArrayList<>());
  }

  public void remove(UUID userId, SseEmitter emitter) {
    if (userId == null || emitter == null) {
      return;
    }

    data.computeIfPresent(userId, (key, emitters) -> {
      emitters.remove(emitter);
      return emitters.isEmpty() ? null : emitters;
    });
  }

  public Map<UUID, List<SseEmitter>> getAll() {
    return new HashMap<>(data);
  }

}
