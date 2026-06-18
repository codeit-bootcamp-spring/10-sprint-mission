package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

  private static final long SSE_TIMEOUT = 1000L * 60 * 60;
  private static final String PING_EVENT_NAME = "sse.ping";

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);

    sseEmitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onError(error -> sseEmitterRepository.delete(receiverId, sseEmitter));

    sseEmitterRepository.save(receiverId, sseEmitter);

    if (!ping(sseEmitter)) {
      sseEmitterRepository.delete(receiverId, sseEmitter);
      return sseEmitter;
    }

    if (lastEventId != null) {
      resendLostMessages(receiverId, lastEventId, sseEmitter);
    }

    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    if (receiverIds == null || receiverIds.isEmpty()) {
      return;
    }

    Set<UUID> distinctReceiverIds = Set.copyOf(receiverIds);
    SseMessage message = sseMessageRepository.save(distinctReceiverIds, eventName, data);

    distinctReceiverIds.forEach(receiverId ->
        sseEmitterRepository.findAllByReceiverId(receiverId)
            .forEach(sseEmitter -> send(receiverId, sseEmitter, message))
    );
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(eventName, data);

    sseEmitterRepository.findAll()
        .forEach((receiverId, sseEmitters) ->
            sseEmitters.forEach(sseEmitter -> send(receiverId, sseEmitter, message))
        );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    Map<UUID, List<SseEmitter>> emitters = sseEmitterRepository.findAll();
    emitters.forEach((receiverId, sseEmitters) ->
        sseEmitters.stream()
            .filter(sseEmitter -> !ping(sseEmitter))
            .forEach(sseEmitter -> sseEmitterRepository.delete(receiverId, sseEmitter))
    );
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name(PING_EVENT_NAME)
          .data("ping"));
      return true;
    } catch (IOException | IllegalStateException e) {
      log.debug("SSE ping failed", e);
      return false;
    }
  }

  private void resendLostMessages(UUID receiverId, UUID lastEventId, SseEmitter sseEmitter) {
    sseMessageRepository.findAllByEventIdAfter(lastEventId, receiverId)
        .forEach(message -> send(receiverId, sseEmitter, message));
  }

  private void send(UUID receiverId, SseEmitter sseEmitter, SseMessage message) {
    try {
      sseEmitter.send(SseEmitter.event()
          .id(message.id().toString())
          .name(message.eventName())
          .data(message.data()));
    } catch (IOException | IllegalStateException e) {
      log.debug("SSE send failed: receiverId={}, eventName={}", receiverId, message.eventName(), e);
      sseEmitterRepository.delete(receiverId, sseEmitter);
    }
  }
}
