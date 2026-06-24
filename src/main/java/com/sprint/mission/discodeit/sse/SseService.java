package com.sprint.mission.discodeit.sse;

import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
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

  private static final Long SSE_TIMEOUT = 1000L * 60 * 60;

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

    sseEmitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onError(error -> sseEmitterRepository.delete(receiverId, emitter));

    // 최초 연결 확인용 더미 이벤트
    if (!ping(emitter)) {
      sseEmitterRepository.delete(receiverId, emitter);
      return emitter;
    }

    // 클라이언트가 마지막으로 받은 이벤트 ID를 보내면, 전의 이벤트를 다시 보내 유실을 복구
    List<SseMessage> missedMessages =
        sseMessageRepository.findAllByReceiverIdAfter(receiverId, lastEventId);

    for (SseMessage message : missedMessages) {
      send(emitter, message);
    }

    log.debug("SSE 연결 ok: receiverId={}, lastEventId={}", receiverId, lastEventId);

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID receiverId : receiverIds) {
      SseMessage message = sseMessageRepository.save(receiverId, eventName, data);

      List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);

      for (SseEmitter emitter : emitters) {
        boolean sent = send(emitter, message);

        if (!sent) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    // broadcast 이벤트도 저장해야 재연결 시 Last-Event-ID 기준으로 복원 가능
    SseMessage message = sseMessageRepository.saveBroadcast(eventName, data);

    List<SseEmitter> emitters = sseEmitterRepository.findAll();

    for (SseEmitter emitter : emitters) {
      boolean sent = send(emitter, message);

      if (!sent) {
        sseEmitterRepository.delete(emitter);
      }
    }
  }

  // 주기적으로 ping
  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    List<SseEmitter> emitters = sseEmitterRepository.findAll();

    for (SseEmitter emitter : emitters) {
      if (!ping(emitter)) {
        sseEmitterRepository.delete(emitter);
      }
    }

    sseMessageRepository.deleteExpiredMessages();
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event().name("ping").data("ping"));

      return true;
    } catch (IOException | IllegalStateException e) {
      return false;
    }
  }

  private boolean send(SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(
          SseEmitter.event()
              .id(message.id().toString())
              .name(message.eventName())
              .data(message.data()));

      return true;
    } catch (IOException | IllegalStateException e) {
      return false;
    }
  }
}
