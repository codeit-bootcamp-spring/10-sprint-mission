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
    /**
     {
        사용자ID: 연결목록
        "user123" : [
                        SseEmitter1,
                        SseEmitter2
                    ]
     }
     한 사용자가 여러 기기 가능하기 때문에 List로 설정.
     ex) user123이 Chrome으로 로그인: SseEmitter1
         user123이 모바일 로그인: SseEmitter2
     **/
    private final ConcurrentMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void save(UUID receiverId, SseEmitter sseEmitter) {
        data.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>())
                .add(sseEmitter);
    }

    /// emitter 삭제
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

    /// 사용자별 emitter 목록 조회
    public List<SseEmitter> findAllByReceiverId(UUID receiverId) {
        return data.getOrDefault(receiverId, List.of());
    }

    /// 사용자Id 목록 조회
    public Collection<UUID> findAllReceiverIds() {
        return data.keySet();
    }
}
