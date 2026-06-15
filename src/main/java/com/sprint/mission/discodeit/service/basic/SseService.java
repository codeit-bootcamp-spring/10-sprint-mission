package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

  private final SseEmitterRepository sseEmitterRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter();
    sseEmitterRepository.save(receiverId, sseEmitter);

    sseEmitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onError(e -> sseEmitterRepository.delete(receiverId, sseEmitter));

    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = sseEmitterRepository.findByReceiverId(receiverId);

      for (SseEmitter emitter : emitters) {
        try {
          // 각 유저를 순회하면서 유저들의 sseEmitter 마다
          // 파라미터로 들어온 값을 통해 이벤트 발행
          emitter.send(
              SseEmitter.event()
                  .name(eventName)
                  .data(data)
          );
        } catch (IOException e) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  // 모든 유저에게 이벤트 전파
  public void broadcast(String eventName, Object data) {
    sseEmitterRepository.findAll().forEach((receiverId, emitters) ->
    {
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.findAll()
        .forEach((receiverId, emitters) -> {
          for (SseEmitter emitter : emitters) {
            if (!ping(emitter)) {
              sseEmitterRepository.delete(receiverId, emitter);
            }
          }
        });
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(
          SseEmitter.event()
              .name("ping")
              .data("ping")
      );
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
