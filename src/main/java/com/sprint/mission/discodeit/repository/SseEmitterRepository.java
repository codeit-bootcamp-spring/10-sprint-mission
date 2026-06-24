package com.sprint.mission.discodeit.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class SseEmitterRepository {

    private final ConcurrentHashMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID receiverId, SseEmitter sseEmitter) {
        // 기존 receiverId가 있다면 가져오고, 없다면 새로 생성 후 sseEmitter 저장
        data.computeIfAbsent(
                        receiverId,
                        id -> new CopyOnWriteArrayList<>()
                )
                .add(sseEmitter);
    }

    public void remove(UUID receiverId, SseEmitter sseEmitter) {
        data.computeIfPresent(
                receiverId,
                (id, sseEmitterList) -> {
                    sseEmitterList.remove(sseEmitter);
                    return sseEmitterList.isEmpty()
                            ? null
                            : sseEmitterList;
                }
        );
    }

    public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
        return data.getOrDefault(receiverId, List.of());
    }

    public Map<UUID, List<SseEmitter>> findAll() {
        return Map.copyOf(data);
    }
}
