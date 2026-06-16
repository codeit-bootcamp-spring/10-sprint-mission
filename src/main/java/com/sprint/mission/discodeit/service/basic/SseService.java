package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import com.sprint.mission.discodeit.dto.sse.SseMessage;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class SseService {

  private static final long SSE_TIMEOUT_MILLIS = Long.MAX_VALUE;
  private static final long HEARTBEAT_DELAY_MILLIS = 25_000L;

  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT_MILLIS);
    sseEmitterRepository.save(receiverId, sseEmitter);

    sseEmitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
    sseEmitter.onError(e -> sseEmitterRepository.delete(receiverId, sseEmitter));

    if (!ping(sseEmitter)) {
      sseEmitterRepository.delete(receiverId, sseEmitter);
    }

    resendMissedMessages(receiverId, lastEventId, sseEmitter);

    return sseEmitter;
  }

  // eventName과 data를 receiverIds 내 유저들에게 event 전파
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = sseMessageRepository.save(eventName, data, receiverIds);

    for (UUID receiverId : receiverIds) {
      List<SseEmitter> emitters = sseEmitterRepository.findByReceiverId(receiverId);

      for (SseEmitter emitter : emitters) {
        try {
          // 각 유저를 순회하면서 유저들의 sseEmitter 마다
          // 파라미터로 들어온 값을 통해 이벤트 발행
          emitter.send(
              SseEmitter.event()
                  .id(message.eventId().toString())
                  .name(message.eventName())
                  .data(message.data())
          );
        } catch (IOException e) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    }
  }

  // 모든 유저에게 이벤트 전파
  public void broadcast(String eventName, Object data) {
    SseMessage message = sseMessageRepository.saveBroadcast(eventName, data);

    sseEmitterRepository.findAll().forEach((receiverId, emitters) ->
    {
      for (SseEmitter emitter : emitters) {
        try {
          emitter.send(SseEmitter.event()
              .id(message.eventId().toString())
              .name(message.eventName())
              .data(message.data()));
        } catch (IOException e) {
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  // 주기적으로 ping 신호를 보내 ping이 false인 emitter들을 정리하는 메소드
  @Scheduled(fixedDelay = HEARTBEAT_DELAY_MILLIS)
  public void cleanUp() {
    sseEmitterRepository.findAll()
        .forEach((receiverId, emitters) -> {
          for (SseEmitter emitter : emitters) {
            if (!ping(emitter)) {
              sseEmitterRepository.delete(receiverId, emitter);
            }
          }
        });
  }

  // emitter에 더미데이터를 전송하고 예외 발생시 false를 리턴하는 메서드
  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(
          SseEmitter.event()
              .name("ping")
              .data("ping")
      );
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  private void resendMissedMessages(UUID receiverId, UUID lastEventId, SseEmitter sseEmitter) {
    if (lastEventId == null) {
      return;
    }

    List<SseMessage> missedMessages = sseMessageRepository.findAllAfter(lastEventId, receiverId);
    for (SseMessage message : missedMessages) {
      try {
        sseEmitter.send(
            SseEmitter.event()
                .id(message.eventId().toString())
                .name(message.eventName())
                .data(message.data())
        );
      } catch (IOException e) {
        sseEmitterRepository.delete(receiverId, sseEmitter);
        return;
      }
    }
  }
}
