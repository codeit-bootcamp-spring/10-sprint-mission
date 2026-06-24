package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public SseEmitter save(UUID receiverId, SseEmitter emitter) {
    data.computeIfAbsent(receiverId, key -> new CopyOnWriteArrayList<>()).add(emitter);
    return emitter;
  }

  public List<SseEmitter> findByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public void deleteByReceiverIdAndEmitter(UUID receiverId, SseEmitter emitter) {
    List<SseEmitter> emitters = data.get(receiverId);
    if (emitters != null) {
      emitters.remove(emitter);
      if (emitters.isEmpty()) {
        data.remove(receiverId, emitters);
      }
    }
  }

  public Map<UUID, List<SseEmitter>> getAll() {
    return data;
  }

  public Set<UUID> getAllReceiverIds() {
    return data.keySet();
  }
}
