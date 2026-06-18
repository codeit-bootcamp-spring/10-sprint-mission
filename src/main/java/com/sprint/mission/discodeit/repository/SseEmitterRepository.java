package com.sprint.mission.discodeit.repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public void save(UUID receiverId, SseEmitter sseEmitter) {
    data.computeIfAbsent(receiverId, key -> new CopyOnWriteArrayList<>())
        .add(sseEmitter);
  }

  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    return List.copyOf(data.getOrDefault(receiverId, List.of()));
  }

  public Map<UUID, List<SseEmitter>> findAll() {
    return data.entrySet().stream()
        .collect(Collectors.toUnmodifiableMap(
            Map.Entry::getKey,
            entry -> List.copyOf(entry.getValue())
        ));
  }

  public void delete(UUID receiverId, SseEmitter sseEmitter) {
    data.computeIfPresent(receiverId, (key, emitters) -> {
      emitters.remove(sseEmitter);
      return emitters.isEmpty() ? null : emitters;
    });
  }
}
