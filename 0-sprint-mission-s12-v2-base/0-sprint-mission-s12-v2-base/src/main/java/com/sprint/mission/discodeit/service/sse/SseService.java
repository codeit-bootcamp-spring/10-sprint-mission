package com.sprint.mission.discodeit.service.sse;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    private static final long TIMEOUT = 1000L * 60 * 60;
    private static final String PING_EVENT_NAME = "ping";

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);
        sseEmitterRepository.save(receiverId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
        sseEmitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, sseEmitter));
        sseEmitter.onError(error -> sseEmitterRepository.delete(receiverId, sseEmitter));

        if (!ping(sseEmitter)) {
            sseEmitterRepository.delete(receiverId, sseEmitter);
            return sseEmitter;
        }
        restoreMissedMessages(receiverId, sseEmitter, lastEventId);
        return sseEmitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = sseMessageRepository.save(receiverIds, eventName, data);
        receiverIds.stream()
                .flatMap(receiverId -> sseEmitterRepository.findAllByReceiverId(receiverId).stream())
                .forEach(sseEmitter -> send(sseEmitter, message));
    }

    public void broadcast(String eventName, Object data) {
        SseMessage message = sseMessageRepository.save(eventName, data);
        sseEmitterRepository.findAll()
                .forEach(sseEmitter -> send(sseEmitter, message));
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        sseEmitterRepository.findAll().stream()
                .filter(sseEmitter -> !ping(sseEmitter))
                .forEach(sseEmitterRepository::delete);
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name(PING_EVENT_NAME)
                    .data("ping"));
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }

    private void restoreMissedMessages(UUID receiverId, SseEmitter sseEmitter, UUID lastEventId) {
        sseMessageRepository.findAllAfter(receiverId, lastEventId)
                .forEach(message -> send(sseEmitter, message));
    }

    private void send(SseEmitter sseEmitter, SseMessage message) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(message.id().toString())
                    .name(message.eventName())
                    .data(message.data()));
        } catch (IOException | IllegalStateException e) {
            log.debug("SSE send failed. emitter will be removed.", e);
            sseEmitterRepository.delete(sseEmitter);
        }
    }
}
