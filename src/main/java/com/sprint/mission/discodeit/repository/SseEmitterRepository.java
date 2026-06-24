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

  public SseEmitter save(UUID receiverId, SseEmitter sseEmitter) {
    data.computeIfAbsent(receiverId, key -> new CopyOnWriteArrayList<>())
        .add(sseEmitter);

    return sseEmitter;
  }

  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public List<SseEmitter> findAll() {
    return data.values().stream()
        .flatMap(List::stream)
        .toList();
  }

  public Map<UUID, List<SseEmitter>> findAllGroupedByReceiverId() {
    return Map.copyOf(data);
  }

  public void delete(UUID receiverId, SseEmitter sseEmitter) {
    List<SseEmitter> emitters = data.get(receiverId);

    if (emitters == null) {
      return;
    }

    emitters.remove(sseEmitter);

    if (emitters.isEmpty()) {
      data.remove(receiverId);
    }
  }

  public void deleteAllByReceiverId(UUID receiverId) {
    data.remove(receiverId);
  }
}