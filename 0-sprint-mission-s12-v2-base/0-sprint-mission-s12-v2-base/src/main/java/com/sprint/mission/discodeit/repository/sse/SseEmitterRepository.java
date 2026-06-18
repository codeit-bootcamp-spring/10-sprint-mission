package com.sprint.mission.discodeit.repository.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitterRepository {

    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save(UUID id, SseEmitter emitter) {
        data.computeIfAbsent(id, key -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
        return data.getOrDefault(receiverId, List.of());
    }

    public Collection<SseEmitter> findAll() {
        return data.values().stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public void delete(UUID receiverId, SseEmitter sseEmitter) {
        List<SseEmitter> emitters = data.get(receiverId);
        if (emitters == null) {
            return;
        }
        emitters.remove(sseEmitter);
        if (emitters.isEmpty()) {
            data.remove(receiverId, emitters);
        }
    }

    public void delete(SseEmitter sseEmitter) {
        data.forEach((receiverId, emitters) -> delete(receiverId, sseEmitter));
    }
}
