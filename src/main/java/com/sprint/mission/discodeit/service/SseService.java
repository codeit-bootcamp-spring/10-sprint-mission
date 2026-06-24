package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private static final long TIMEOUT = 60L * 60 * 1000;

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(TIMEOUT);
    sseEmitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> {
      log.debug("SSE 연결 완료 (receiverId={})", receiverId);
      sseEmitterRepository.deleteByReceiverIdAndEmitter(receiverId, emitter);
    });
    emitter.onTimeout(() -> {
      log.debug("SSE 연결 타임아웃 (receiverId={})", receiverId);
      emitter.complete();
    });
    emitter.onError(e -> {
      log.warn("SSE 연결 오류 (receiverId={})", receiverId, e);
      sseEmitterRepository.deleteByReceiverIdAndEmitter(receiverId, emitter);
    });

    try {
      emitter.send(SseEmitter.event()
          .id(UUID.randomUUID().toString())
          .name("sse.connected")
          .data("connected: " + receiverId));
    } catch (IOException e) {
      sseEmitterRepository.deleteByReceiverIdAndEmitter(receiverId, emitter);
    }

    if (lastEventId != null) {
      sseMessageRepository.findAllAfter(lastEventId).stream()
          .filter(message -> message.isTargetedTo(receiverId))
          .forEach(message -> sendToEmitter(receiverId, emitter, message));
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(SseMessage.of(eventName, data, receiverIds));
    for (UUID receiverId : receiverIds) {
      for (SseEmitter emitter : sseEmitterRepository.findByReceiverId(receiverId)) {
        sendToEmitter(receiverId, emitter, message);
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(SseMessage.broadcast(eventName, data));
    for (UUID receiverId : sseEmitterRepository.getAllReceiverIds()) {
      for (SseEmitter emitter : sseEmitterRepository.findByReceiverId(receiverId)) {
        sendToEmitter(receiverId, emitter, message);
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    sseEmitterRepository.getAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        if (!ping(emitter)) {
          sseEmitterRepository.deleteByReceiverIdAndEmitter(receiverId, emitter);
        }
      }
    });
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(SseEmitter.event().comment("keep-alive"));
      return true;
    } catch (IOException | IllegalStateException e) {
      emitter.complete();
      return false;
    }
  }

  private void sendToEmitter(UUID receiverId, SseEmitter emitter, SseMessage message) {
    try {
      emitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data(), MediaType.APPLICATION_JSON));
    } catch (IOException | IllegalStateException e) {
      sseEmitterRepository.deleteByReceiverIdAndEmitter(receiverId, emitter);
    }
  }
}
