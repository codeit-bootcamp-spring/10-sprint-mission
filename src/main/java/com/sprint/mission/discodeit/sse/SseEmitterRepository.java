package com.sprint.mission.discodeit.sse;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class SseEmitterRepository {

  private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

  public void save(UUID receiverId, SseEmitter emitter) {
    data.compute(
        receiverId,
        (key, emitters) -> {
          List<SseEmitter> newEmitters =
              emitters == null ? new ArrayList<>() : new ArrayList<>(emitters);

          newEmitters.add(emitter);
          return newEmitters;
        });
  }

  public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
    return data.getOrDefault(receiverId, List.of());
  }

  public List<SseEmitter> findAll() {
    return data.values().stream().flatMap(List::stream).toList();
  }

  public void delete(UUID receiverId, SseEmitter emitter) {
    data.computeIfPresent(
        receiverId,
        (key, emitters) -> {
          List<SseEmitter> newEmitters = new ArrayList<>(emitters);
          newEmitters.remove(emitter);

          return newEmitters.isEmpty() ? null : newEmitters;
        });
  }

  public void delete(SseEmitter emitter) {
    data.forEach((receiverId, emitters) -> delete(receiverId, emitter));
  }
}
