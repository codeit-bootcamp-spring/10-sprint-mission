package com.sprint.mission.discodeit.service.basic;


import static java.util.Objects.requireNonNull;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
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
public class BasicSseService implements SseService {
  private static final Long SSE_TIMEOUT = 1000L * 60 * 60;

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  @Override
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    requireNonNull(receiverId, "receiverId");

    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);


    emitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onError(e -> emitterRepository.delete(receiverId, emitter));

    boolean connected = ping(emitter);

    if (!connected) {
      emitterRepository.delete(receiverId, emitter);
      return emitter;
    }

    if (lastEventId != null) {
      List<SseMessage> missedMessages = messageRepository.findAllAfter(lastEventId);

      for (SseMessage message : missedMessages) {
        try{
          emitter.send(
              SseEmitter.event()
                  .id(message.id().toString())
                  .name(message.name())
                  .data(message.data())
          );
        } catch (Exception e) {
          log.debug("failed to send SseEmitter message receiverId = {}, eventId = {}", receiverId, message.id(), e);
          emitterRepository.delete(receiverId, emitter);
          break;
        }
      }
    }
    return emitter;
  }

  @Override
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    requireNonNull(receiverIds, "receiverIds");
    requireNonNull(eventName, "eventName");

    SseMessage message = SseMessage.of(eventName, data);
    messageRepository.save(message);

    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = emitterRepository.findAllByReceiverId(receiverId);

      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(
              SseEmitter.event()
                  .id(message.id().toString())
                  .name(message.name())
                  .data(message.data())
          );
        } catch (Exception e) {
          log.debug(
              "failed to send SSE message. receiverId={}, eventId={}",
              receiverId,
              message.id(),
              e
          );
          emitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  @Override
  public void broadcast(String eventName, Object data) {
    requireNonNull(eventName, "eventName");

    SseMessage message = SseMessage.of(eventName, data);
    messageRepository.save(message);

    List<SseEmitter> emitters = emitterRepository.findAll();

    for (SseEmitter emitter : emitters) {
      try {
        emitter.send(
            SseEmitter.event()
                .id(message.id().toString())
                .name(message.name())
                .data(message.data())
        );
      } catch (Exception e) {
        log.debug("failed to broadcast SSE message. eventId={}", message.id(), e);
      }
    }
  }

  @Override
  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    Map<UUID, List<SseEmitter>> groupedEmitters =
        emitterRepository.findAllGroupedByReceiverId();

    groupedEmitters.forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        boolean alive = ping(emitter);

        if (!alive) {
          emitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(
          SseEmitter.event()
              .name("ping")
              .data("connected")
      );

      return true;
    } catch (Exception e){
      log.debug("SSE ping failed. emitter = {}", emitter, e);
      return false;
    }

  }
}
