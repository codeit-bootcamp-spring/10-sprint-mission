package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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

  private static final long TIMEOUT = 1000L * 60 * 60; // 1시간

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(TIMEOUT);

    emitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, emitter));
    emitter.onError(e -> sseEmitterRepository.delete(receiverId, emitter));

    sseEmitterRepository.save(receiverId, emitter);

    if (!ping(emitter)) {
      sseEmitterRepository.delete(receiverId, emitter);
      return emitter;
    }

    // lastEventId 이후 유실된 메시지 재전송
    if (lastEventId != null) {
      List<SseMessage> missed = sseMessageRepository.findAllAfter(lastEventId);
      for (SseMessage message : missed) {
        try {
          emitter.send(SseEmitter.event()
              .id(message.getId().toString())
              .name(message.getEventName())
              .data(message.getData()));
        } catch (IOException e) {
          sseEmitterRepository.delete(receiverId, emitter);
          return emitter;
        }
      }
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(SseMessage.of(eventName, data));
    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);
      for (SseEmitter emitter : emitters) {
        sendToEmitter(receiverId, emitter, message);
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(SseMessage.of(eventName, data));
    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        sendToEmitter(receiverId, emitter, message);
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    for (Map.Entry<UUID, List<SseEmitter>> entry : sseEmitterRepository.findAll().entrySet()) {
      UUID receiverId = entry.getKey();
      List<SseEmitter> emitters = entry.getValue();
      for (SseEmitter emitter : emitters) {
        if (!ping(emitter)) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping").data(""));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void sendToEmitter(UUID receiverId, SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.getId().toString())
          .name(message.getEventName())
          .data(message.getData()));
    } catch (IOException e) {
      sseEmitterRepository.delete(receiverId, emitter);
    }
  }
}
