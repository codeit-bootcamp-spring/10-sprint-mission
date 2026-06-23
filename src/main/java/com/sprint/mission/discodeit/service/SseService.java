package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessage;
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

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    log.info("Connecting SSE for user: {}, lastEventId: {}", receiverId, lastEventId);
    SseEmitter sseEmitter = new SseEmitter(1800000L);

    sseEmitter.onCompletion(() -> {
      log.debug("SSE connection completed for user {}", receiverId);
      sseEmitterRepository.delete(receiverId, sseEmitter);
    });
    sseEmitter.onTimeout(() -> {
      log.debug("SSE connection timeout for user {}", receiverId);
      sseEmitter.complete();
      sseEmitterRepository.delete(receiverId, sseEmitter);
    });
    sseEmitter.onError((e) -> {
      log.error("SSE connection error for user {}", receiverId, e);
      sseEmitter.complete();
      sseEmitterRepository.delete(receiverId, sseEmitter);
    });

    sseEmitterRepository.save(receiverId, sseEmitter);

    ping(sseEmitter);

    if (lastEventId != null) {
      List<SseMessage> unsentMessages = sseMessageRepository.findUnsentMessages(receiverId, lastEventId);
      for (SseMessage message : unsentMessages) {
        try {
          sseEmitter.send(SseEmitter.event()
              .id(message.id().toString())
              .name(message.eventName())
              .data(message.data()));
        } catch (IOException e) {
          log.warn("Failed to recover unsent SSE event {} to user {}", message.eventName(), receiverId, e);
          sseEmitter.complete();
          sseEmitterRepository.delete(receiverId, sseEmitter);
          break;
        }
      }
    }

    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID receiverId : receiverIds) {
      UUID eventId = UUID.randomUUID();
      SseMessage message = new SseMessage(eventId, receiverId, eventName, data);
      sseMessageRepository.save(message);

      List<SseEmitter> emitters = sseEmitterRepository.findByReceiverId(receiverId);
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          log.warn("Failed to send SSE event {} to user {}", eventName, receiverId, e);
          emitter.complete();
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    SseMessage message = new SseMessage(eventId, null, eventName, data);
    sseMessageRepository.save(message);

    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(eventId.toString())
              .name(eventName)
              .data(data));
        } catch (IOException e) {
          log.warn("Failed to broadcast SSE event {} to user {}", eventName, receiverId, e);
          emitter.complete();
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    log.debug("Starting SSE emitters cleanup");
    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        if (!ping(emitter)) {
          log.info("Removing inactive SseEmitter for user {}", receiverId);
          emitter.complete();
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name("ping")
          .data("ping"));
      return true;
    } catch (IOException e) {
      log.debug("Failed to send ping: emitter is likely inactive/closed.");
      return false;
    }
  }
}
