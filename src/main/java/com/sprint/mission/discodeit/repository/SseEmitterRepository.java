package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Repository
public class SseEmitterRepository {
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID receiverId, SseEmitter emitter) {
        data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(emitter);
    }

    public List<SseEmitter> findAllByUserId(UUID receiverId) {
        return data.getOrDefault(receiverId, List.of());
    }

    public List<SseEmitter> findAllEmitters() {
        return data.values().stream().flatMap(List::stream).toList();
    }

    public Map<UUID, List<SseEmitter>> findAll() {
        return data;
    }

    public void remove(UUID receiverId, SseEmitter emitter) {
        data.computeIfPresent(receiverId, (k, v) -> {
            v.remove(emitter);
            // 리스트가 비었으면 null을 반환하여 Map에서 Key 자체를 제거
            return v.isEmpty() ? null : v;
        });
    }
}
