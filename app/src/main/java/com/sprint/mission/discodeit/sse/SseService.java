package com.sprint.mission.discodeit.sse;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

  private final long SSE_TIMEOUT = 1000L * 60 * 5;  // 5분
  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);
    sseEmitterRepository.save(receiverId, emitter);

    // 연결 종료시
    emitter.onCompletion(() -> {
      log.info("SSE 연결완료: userId={}", receiverId);
      sseEmitterRepository.delete(receiverId, emitter);
    });
    // 타임아웃 발생시
    emitter.onTimeout(() -> {
      log.info("SSE 타임아웃: userId={}", receiverId);
      sseEmitterRepository.delete(receiverId, emitter);
    });
    // 네트워크 에러시
    emitter.onError((ex) -> {
      log.info("SSE 네트워크 오류: userId={}, error={}", receiverId, ex.getMessage());
      sseEmitterRepository.delete(receiverId, emitter);
    });

    try {
      String connectedData = lastEventId != null ? "reconnected!" : "connected";
      emitter.send(SseEmitter.event()
          .name(SseMessageType.CONNECTED.getValue())
          .data(connectedData));
      // 이벤트 유실 복원
      if (lastEventId != null) {
        for (SseMessage msg : sseMessageRepository
            .getMessagesAfterLastEventId(receiverId, lastEventId)) {
          emitter.send(SseEmitter.event()
              .id(msg.getId().toString())
              .name(msg.getEventName())
              .data(msg.getData()));
        }
      }
    } catch (IOException e) {
      log.warn("SSE 연결/유실복구 전송 실패: userId={}", receiverId);
      sseEmitterRepository.delete(receiverId, emitter);
    }

    return emitter;
  }

  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    SseMessage message = saveMessage(receiverIds, eventName, data, false);
    sendMessage(message, sseEmitterRepository.getUsersEmitter(receiverIds));
  }

  public void broadcast(String eventName, Object data) {
    SseMessage message = saveMessage(null, eventName, data, true);
    sendMessage(message, sseEmitterRepository.getAllEmitter());
  }

  private SseMessage saveMessage(Collection<UUID> receiverIds, String eventName, Object data,
      boolean isBroadcast) {
    SseMessage message = SseMessage.create(receiverIds, eventName, data, isBroadcast);
    sseMessageRepository.save(message);
    return message;
  }

  private void sendMessage(SseMessage message, ConcurrentMap<UUID, List<SseEmitter>> emitters) {
    emitters.forEach((receiverId, userSseEmitterList) ->
        userSseEmitterList.forEach(emitter -> {
          try {
            emitter.send(SseEmitter.event()
                .id(message.getId().toString())
                .name(message.getEventName())
                .data(message.getData()));
          } catch (IOException e) {
            sseEmitterRepository.delete(receiverId, emitter);
          }
        })
    );
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30) // 30분
  public void cleanUp() {
    log.info("SSE 청소 스케줄링 시작");
    ConcurrentMap<UUID, List<SseEmitter>> data = sseEmitterRepository.getAllEmitter();

    data.forEach((receiverId, userSseEmitterList) -> {
      for (SseEmitter emitter : userSseEmitterList) {
        boolean isAlive = ping(emitter);

        if (!isAlive) {
          log.debug("만료된 커넥션 삭제: userId={}, emitter={}", receiverId, emitter);
          sseEmitterRepository.delete(receiverId, emitter);
        }
      }
    });
  }

  private boolean ping(SseEmitter sseEmitter) {
    try {
      sseEmitter.send(SseEmitter.event()
          .name(SseMessageType.PING.getValue())
          .data("ping!"));
      return true;
    } catch (IOException e) {
      return false;
    }
  }
}
