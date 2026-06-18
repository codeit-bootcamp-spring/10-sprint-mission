package com.sprint.mission.discodeit.sse;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
    sseEmitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, emitter));
    emitter.onError(e -> sseEmitterRepository.remove(receiverId, emitter));

    // 최초 연결 확인용 더미 이벤트
    ping(emitter);

    // lastEventId 이후 유실된 이벤트 복원
    if (lastEventId != null) {
      sseMessageRepository.findAllAfter(lastEventId).stream()
          .filter(message -> message.receiverIds() == null
              || message.receiverIds().contains(receiverId))
          .forEach(message -> sendToEmitter(emitter, message));
    }
    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = SseMessage.of(eventName, data, receiverIds);
    sseMessageRepository.save(message);

    for (UUID receiverId : receiverIds) {
      sseEmitterRepository.findAllByReceiverId(receiverId)
          .forEach(emitter -> sendToEmitter(emitter, message));
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = SseMessage.of(eventName, data, null);
    sseMessageRepository.save(message);

    sseEmitterRepository.findAll().values()
        .forEach(emitters -> emitters.forEach(emitter -> sendToEmitter(emitter, message)));
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.findAll()
        .forEach((receiverId, emitters) -> emitters.removeIf(emitter -> !ping(emitter)));
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping").data("ping"));
      return true;
    } catch (IOException e) {
      sseEmitter.complete();
      return false;
    }
  }

  private void sendToEmitter(SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data()));
    } catch (IOException e) {
      emitter.complete();
    }
  }

}
