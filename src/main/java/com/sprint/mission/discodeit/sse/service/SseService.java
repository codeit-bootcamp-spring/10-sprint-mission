package com.sprint.mission.discodeit.sse.service;

import com.sprint.mission.discodeit.sse.SseMessage;
import com.sprint.mission.discodeit.sse.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.sse.repository.SseMessageRepository;
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

  private final SseMessageRepository sseMessageRepository;
  private final SseEmitterRepository sseEmitterRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter();
    sseEmitterRepository.save(receiverId, sseEmitter);

    sseEmitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, sseEmitter));
    sseEmitter.onError(e -> sseEmitterRepository.remove(receiverId, sseEmitter));

    if (lastEventId != null) {
      List<SseMessage> result = sseMessageRepository.findMessagesAfter(receiverId, lastEventId);
      for (SseMessage sseMessage : result) {
        try {
          sseEmitter.send(SseEmitter.event()
              .id(sseMessage.id().toString())
              .name(sseMessage.name())
              .data(sseMessage.data()));
        } catch (Exception e) {
          sseEmitterRepository.remove(receiverId, sseEmitter);
          break;
        }
      }
    }
    if (!ping(sseEmitter)) {
      sseEmitterRepository.remove(receiverId, sseEmitter);
    }
    return sseEmitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    for (UUID userId : receiverIds) {
      UUID messageId = UUID.randomUUID();
      SseMessage message = new SseMessage(messageId, userId, eventName, data);
      sseMessageRepository.save(message);

      List<SseEmitter> sseEmitters = sseEmitterRepository.get(userId);
      for (SseEmitter sseEmitter : sseEmitters) {
        try {
          sseEmitter.send(SseEmitter.event()
              .id(messageId.toString())
              .name(eventName)
              .data(data));
        } catch (Exception e) {
          sseEmitterRepository.remove(userId, sseEmitter);
          break;
        }
      }
    }
  }

  public void broadcast(String eventName, Object data) {
    Map<UUID, List<SseEmitter>> allEmitters = sseEmitterRepository.getAll();

    for (Map.Entry<UUID, List<SseEmitter>> entry : allEmitters.entrySet()) {
      UUID userId = entry.getKey();
      List<SseEmitter> sseEmitters = entry.getValue();

      UUID messageId = UUID.randomUUID();
      SseMessage message = new SseMessage(messageId, userId, eventName, data);
      sseMessageRepository.save(message);

      for (SseEmitter sseEmitter : sseEmitters) {
        try {
          sseEmitter.send(SseEmitter.event()
              .id(messageId.toString())
              .name(eventName)
              .data(data));
        } catch (Exception e) {
          sseEmitterRepository.remove(userId, sseEmitter);
          break;
        }
      }
    }
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  public void cleanUp() {
    Map<UUID, List<SseEmitter>> allEmitters = sseEmitterRepository.getAll();
    for (Map.Entry<UUID, List<SseEmitter>> entry : allEmitters.entrySet()) {
      UUID userId = entry.getKey();
      List<SseEmitter> sseEmitters = entry.getValue();
      for (SseEmitter sseEmitter : sseEmitters) {
        if (!ping(sseEmitter)) {
          sseEmitterRepository.remove(userId, sseEmitter);
        }
      }
    }
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event().name("ping"));
      return true;
    } catch (Exception e) {
      return false;
    }
  }

}
