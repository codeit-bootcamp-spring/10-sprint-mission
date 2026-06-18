package com.sprint.mission.discodeit.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {

    private final ConcurrentHashMap<UUID, List<SseEmitter>> data = new ConcurrentHashMap<>();

    public void add(UUID receiverId, SseEmitter sseEmitter){
        data.computeIfAbsent(receiverId, k -> new CopyOnWriteArrayList<>()).add(sseEmitter);
    }

    public void remove(UUID receiverId, SseEmitter sseEmitter){
        List<SseEmitter> emitters = data.get(receiverId);
        if(emitters != null){
            emitters.remove(sseEmitter);
            if(emitters.isEmpty()){
                data.remove(receiverId);
            }
        }
    }

    public List<SseEmitter> findAllByReceiverId(UUID receiverId){
        return data.getOrDefault(receiverId, new CopyOnWriteArrayList<>());
    }

    // 브로드 캐스트용
    public List<SseEmitter> findAll(){
        return data.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public List<UUID> findAllReceiverIds(){
        return data.keySet().stream().toList();
    }


}
