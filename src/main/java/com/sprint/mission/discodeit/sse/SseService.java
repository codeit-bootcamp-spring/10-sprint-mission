package com.sprint.mission.discodeit.sse;

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

  private static final long TIMEOUT = 1000L * 60 * 60;

  private final SseEmitterRepository emitterRepository;
  private final SseMessageRepository messageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {

    SseEmitter emitter = new SseEmitter(TIMEOUT);

    emitterRepository.save(receiverId, emitter);

    emitter.onCompletion(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onTimeout(() -> emitterRepository.delete(receiverId, emitter));
    emitter.onError(error -> emitterRepository.delete(receiverId, emitter));

    ping(emitter);

    if (lastEventId != null) {
      List<SseMessage> missedMessages = messageRepository.findAfter(lastEventId);

      for (SseMessage message : missedMessages) {
        boolean canReceive =
            message.isBroadcast()
                || (
                message.getReceiverIds() != null
                    && message.getReceiverIds().contains(receiverId)
            );
        if (!canReceive) {
          continue;
        }
        try {
          emitter.send(
              SseEmitter.event()
                  .id(message.getId().toString())
                  .name(message.getEventName())
                  .data(message.getData())
          );
          log.info(
              "[SUCCESS] SSE Replay Sent: receiverId={}, eventName={}, eventId={}",
              receiverId,
              message.getEventName(),
              message.getId()
          );
        } catch (IOException e) {
          emitterRepository.delete(receiverId, emitter);

          log.warn(
              "[FAILED] SSE Replay Failed: receiverId={}, eventName={}, eventId={}",
              receiverId,
              message.getEventName(),
              message.getId()
          );
          break;
        }
      }
    }
    log.info("[SUCCESS] SSE Connected: receiverId={}", receiverId);

    return emitter;
  }

  public void send(
      Collection<UUID> receiverIds,
      String eventName,
      Object data
  ) {
    UUID eventId = UUID.randomUUID();

    SseMessage message = SseMessage.builder()
        .id(eventId)
        .receiverIds(receiverIds)
        .broadcast(false)
        .eventName(eventName)
        .data(data)
        .build();

    messageRepository.save(message);

    receiverIds.forEach(receiverId -> {
      List<SseEmitter> emitters =
          emitterRepository.findByReceiverId(receiverId);

      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(
              SseEmitter.event()
                  .id(eventId.toString())
                  .name(eventName)
                  .data(data)
          );
          log.info("[SUCCESS] SSE Event Sent: receiverId={}, eventName={}", receiverId, eventName);

        } catch (IOException e) {
          emitterRepository.delete(receiverId, emitter);

          log.warn("[FAILED] SSE Event Send Failed: receiverId={}, eventName={}", receiverId,
              eventName
          );
        }
      }
    });
  }

  public void broadcast(
      String eventName,
      Object data
  ) {

    UUID eventId = UUID.randomUUID();

    SseMessage message = SseMessage.builder()
        .id(eventId)
        .receiverIds(null)
        .broadcast(true)
        .eventName(eventName)
        .data(data)
        .build();

    messageRepository.save(message);

    emitterRepository.findAll()
        .forEach((receiverId, emitters) -> {

          for (SseEmitter emitter : emitters) {
            try {
              emitter.send(
                  SseEmitter.event()
                      .id(eventId.toString())
                      .name(eventName)
                      .data(data)
              );
              log.info("[SUCCESS] SSE Broadcast Sent: receiverId={}, eventName={}", receiverId,
                  eventName);

            } catch (IOException e) {
              emitterRepository.delete(receiverId, emitter);

              log.warn("[FAILED] SSE Broadcast Failed: receiverId={}, eventName={}", receiverId,
                  eventName);
            }
          }
        });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {

    emitterRepository.findAll()
        .forEach((receiverId, emitters) -> {

          emitters.removeIf(emitter -> {

            boolean alive = ping(emitter);

            if (!alive) {
              emitterRepository.delete(receiverId, emitter);
            }

            return !alive;
          });
        });
  }

  private boolean ping(SseEmitter emitter) {
    try {
      emitter.send(
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
