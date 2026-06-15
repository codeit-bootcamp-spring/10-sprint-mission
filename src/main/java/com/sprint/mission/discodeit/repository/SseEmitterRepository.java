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

  public Map<UUID, List<SseEmitter>> findAll() {
    return data;
  }

  // 수신자 ID를 통해 SseEmitter 목록을 반환하는 메서드
  public List<SseEmitter> findByReceiverId(UUID receiverId) {
    List<SseEmitter> emitters = data.get(receiverId);
    return emitters.isEmpty() ? null : emitters;
  }

  // 레포지토리 내 Map 자료구조에 receiverId가 없으면 새로운 리스트를 생성,
  // receiverId가 존재하면 해당 id에 해당하는 value에 sseEmitter를 추가
  public void save(UUID receiverId, SseEmitter sseEmitter) {
    data.computeIfAbsent(receiverId, id -> new CopyOnWriteArrayList<>())
        .add(sseEmitter);
  }

  public void delete(UUID receiverId, SseEmitter sseEmitter) {
    List<SseEmitter> emitters = data.get(receiverId); // 수신자의 sseEmitters 목록 조회

    // emitters가 없으면 지울 emitter가 없으므로 return
    if (emitters == null) {
      return;
    }

    // emitter 목록에서 삭제하고자 하는 emitter 삭제
    emitters.remove(sseEmitter);

    // 삭제 후 emitter 목록이 없으면 불필요한 메모리 공간을 확보하기 위해 해당 수신자의 빈 emitter list를 삭제
    if (emitters.isEmpty()) {
      data.remove(receiverId);
    }
  }

}
