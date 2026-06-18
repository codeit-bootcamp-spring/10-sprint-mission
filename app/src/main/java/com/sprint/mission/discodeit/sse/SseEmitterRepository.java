package com.sprint.mission.discodeit.sse;

import java.util.Collection;
import java.util.List;
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

  public ConcurrentMap<UUID, List<SseEmitter>> getAllEmitter() {
    return data;
  }

  public ConcurrentMap<UUID, List<SseEmitter>> getUsersEmitter(Collection<UUID> receiverIds) {
    return receiverIds.stream()
        .filter(data::containsKey)
        .collect(Collectors.toConcurrentMap(
            receiverId -> receiverId,
            data::get
        ));
  }

  public void save(UUID receiverId, SseEmitter emitter) {
    data.compute(receiverId, (k, v) -> {
      if (v == null) {
        v = new CopyOnWriteArrayList<>();
      }
      v.add(emitter);
      return v;
    });
  }

  public void delete(UUID receiverId, SseEmitter emitter) {
    data.computeIfPresent(receiverId, (k, v) -> {
      v.remove(emitter);
      return v.isEmpty() ? null : v;
    });
  }
}
