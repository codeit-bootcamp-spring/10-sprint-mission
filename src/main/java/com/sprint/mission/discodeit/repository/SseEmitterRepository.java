package com.sprint.mission.discodeit.repository;


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

  public void add(UUID id, SseEmitter emitter) {
    data.computeIfAbsent(id, k -> new CopyOnWriteArrayList<>()).add(emitter);
  }

  public Map<UUID, List<SseEmitter>> findAll() {
    return data;
  }

  public void delete(UUID id, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(id);
    if (emitters != null) {
      emitters.remove(emitter);
      if (emitters.isEmpty()) {
        data.remove(id);
      }
    }
  }
}
