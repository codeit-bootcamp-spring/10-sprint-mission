package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository.SseMessage;
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

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;
  private static final long TIMEOUT = 60L * 60L * 1000L;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {

    SseEmitter emitter = new SseEmitter(TIMEOUT);

    emitter.onCompletion(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onError((e) -> emitterRepository.delete(receiverId, emitter));

    emitterRepository.add(receiverId, emitter);

    UUID eventId = UUID.randomUUID();
    // 연결 직후
    try {
      emitter.send(
          SseEmitter.event()
              .name("connected")                         // 이벤트 이름
              .data(Map.of("receiverId", receiverId))         // 초기 데이터 (clientId 반환)
      );
    } catch (Exception e) {
      // 연결 종료 처리
      emitter.completeWithError(e);
      throw new RuntimeException("SSE 최초 연결 중 서버 에러 발생", e);
    }

    if (lastEventId != null) {
      List<SseMessage> lostMessages = messageRepository.findAllByReceiverIdAfterEventId(receiverId,
          lastEventId);
      for (SseMessage message : lostMessages) {
        try {
          emitter.send(SseEmitter.event()
              .id(message.eventId().toString())
              .name(message.eventName())
              .data(message.data()));
        } catch (Exception e) {
          emitter.completeWithError(e);
          break;
        }
      }

    }
    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    messageRepository.save(eventId, new SseMessage(eventId, receiverIds, eventName, data));

    emitterRepository.findAll().entrySet().stream()
        .filter(e -> receiverIds.contains(e.getKey()))
        .forEach(e -> {
          e.getValue().forEach(emitter -> {
            try {
              emitter.send(
                  SseEmitter.event()
                      .id(eventId.toString())
                      .name(eventName)
                      .data(data)
              );
            } catch (Exception ex) {
              emitter.completeWithError(ex);
            }
          });
        });
  }

  public void broadcast(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();

    messageRepository.save(eventId,
        new SseMessage(eventId, emitterRepository.findAll().keySet(), eventName, data));
    emitterRepository.findAll().forEach((key, value) -> value.forEach(emitter -> {
      try {
        emitter.send(
            SseEmitter.event()
                .id(eventId.toString())
                .name(eventName)
                .data(data)
        );
      } catch (Exception ex) {
        emitter.completeWithError(ex);
      }
    }));
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    emitterRepository.findAll().forEach((key, value) -> value.forEach(emitter -> {
      if (!ping(emitter)) {
        emitter.completeWithError(new RuntimeException("핑 검증 실패"));
      }
    }));
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name("ping")
          .data("pong"));
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
