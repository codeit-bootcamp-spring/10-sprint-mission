package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository.SseMessage;
import com.sprint.mission.discodeit.service.SseService;
import jakarta.annotation.PreDestroy;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.SseEventBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

  private static final long TIMEOUT = 60L * 60L * 1000L;
  private final SseEmitterRepository sseEmitterRepository;
  private final SseMessageRepository sseMessageRepository;

  private final ExecutorService senderPool = new ThreadPoolExecutor(
      2, 16, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(10000)
  );

  public boolean tryEnqueue(Runnable task) {
    try {
      if (((ThreadPoolExecutor) senderPool).getQueue().remainingCapacity() > 1000) {
        senderPool.submit(task);
        return true;
      }
      log.warn("[SSE] 백프레셔 드랍");
      return false;
    } catch (RejectedExecutionException e) {
      log.error("[SSE] 작업 거절됨");
      return false;
    }
  }

  @PreDestroy
  public void shutdown() {
    log.info("[SSE] SSE 스레드풀 종료");
    senderPool.shutdown();
    try {
      if (!senderPool.awaitTermination(5, TimeUnit.SECONDS)) {
        senderPool.shutdownNow();
      }
    } catch (InterruptedException e) {
      senderPool.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  @Override
  public SseEmitter connect(UUID receiverId, UUID lastEventId) {
    log.info("[SSE] SSE 연결 요청 receiverId={}", receiverId);
    SseEmitter emitter = new SseEmitter(TIMEOUT);
    sseEmitterRepository.save(receiverId, emitter);
    if (lastEventId != null) {
      for (SseMessage message : sseMessageRepository.findAllAfter(lastEventId)) {
        sendToEmitter(
            receiverId,
            emitter,
            message.getEventId(),
            message.getEventName(),
            message.getData()
        );
      }
    } else {
      sendSystemEvent(
          receiverId,
          emitter,
          "connect",
          "EventStream Created. [receiverId=" + receiverId + "]"
      );
    }
    return emitter;
  }

  @Override
  public void send(Collection<UUID> receiverIds, String eventName, Object data) {
    log.info("[SSE] SSE send 요청 receiverCount={}", receiverIds.size());
    String eventIdStr = saveMessageAndGetId(eventName, data).toString();
    for (UUID receiverId : receiverIds) {
      for (SseEmitter emitter : sseEmitterRepository.findByReceiverId(receiverId)) {
        sendToEmitter(receiverId, emitter, eventIdStr, eventName, data);
      }
    }
  }

  @Override
  public void broadcast(String eventName, Object data) {
    log.info("[SSE] SSE broadcast 요청");
    String eventIdStr = saveMessageAndGetId(eventName, data).toString();
    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        sendToEmitter(receiverId, emitter, eventIdStr, eventName, data);
      }
    });
  }

  @Scheduled(fixedDelay = 1000 * 60 * 30)
  @Override
  public void cleanUp() {
    log.info("[SSE] SseEmitter 객체 스케줄러 작동");
    sseEmitterRepository.findAll().forEach((receiverId, emitters) -> {
      for (SseEmitter emitter : emitters) {
        sendSystemEvent(receiverId, emitter, "ping", "");
      }
    });
  }

  private UUID saveMessageAndGetId(String eventName, Object data) {
    UUID eventId = UUID.randomUUID();
    sseMessageRepository.save(eventId, new SseMessage(eventId.toString(), eventName, data));
    return eventId;
  }

  private void sendToEmitter(UUID receiverId, SseEmitter emitter, String eventId, String eventName,
      Object data) {
    tryEnqueue(() -> {
      try {
        SseEventBuilder event = SseEmitter.event()
            .id(eventId)
            .name(eventName)
            .data(data, MediaType.APPLICATION_JSON);
        emitter.send(event);
      } catch (IOException e) {
        log.error("[SSE] 전송 실패 및 파이프 폐기: receiverId={}", receiverId, e);
        sseEmitterRepository.delete(receiverId, emitter);
      }
    });
  }

  private void sendSystemEvent(UUID receiverId, SseEmitter emitter, String eventName, String data) {
    tryEnqueue(() -> {
      try {
        SseEventBuilder event = SseEmitter.event()
            .name(eventName)
            .data(data, MediaType.TEXT_PLAIN);
        emitter.send(event);
      } catch (IOException e) {
        log.error("[SSE] 시스템 이벤트 전송 실패 및 파이프 폐기: receiverId={}, eventName={}", receiverId,
            eventName, e);
        sseEmitterRepository.delete(receiverId, emitter);
      }
    });
  }
}
