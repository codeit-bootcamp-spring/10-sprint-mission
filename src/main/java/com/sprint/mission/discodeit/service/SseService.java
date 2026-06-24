package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessage;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import java.io.IOException;
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

    private static final long SSE_TIMEOUT = 1000L * 60 * 60;

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        sseEmitterRepository.save(receiverId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, emitter));
        emitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, emitter));
        emitter.onError(ex -> sseEmitterRepository.delete(receiverId, emitter));

        if (!ping(emitter)) {
            sseEmitterRepository.delete(receiverId, emitter);
            return emitter;
        }

        if (lastEventId != null) {
            List<SseMessage> lostMessages = sseMessageRepository.findAllAfter(lastEventId, receiverId);
            lostMessages.forEach(message -> sendInternal(receiverId, emitter, message));
        }

        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = new SseMessage(
                UUID.randomUUID(),
                eventName,
                data,
                List.copyOf(receiverIds),
                false
        );

        sseMessageRepository.save(message);

        for (UUID receiverId : receiverIds) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);
            emitters.forEach(emitter -> sendInternal(receiverId, emitter, message));
        }
    }

    public void broadcast(String eventName, Object data) {
        SseMessage message = new SseMessage(
                UUID.randomUUID(),
                eventName,
                data,
                List.of(),
                true
        );

        sseMessageRepository.save(message);

        for (Map.Entry<UUID, List<SseEmitter>> entry : sseEmitterRepository.findAll().entrySet()) {
            UUID receiverId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();

            emitters.forEach(emitter -> sendInternal(receiverId, emitter, message));
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        for (Map.Entry<UUID, List<SseEmitter>> entry : sseEmitterRepository.findAll().entrySet()) {
            UUID receiverId = entry.getKey();
            List<SseEmitter> emitters = entry.getValue();

            emitters.forEach(emitter -> {
                if (!ping(emitter)) {
                    sseEmitterRepository.delete(receiverId, emitter);
                }
            });
        }
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("ping")
                    .data("keep-alive"));
            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }

    private void sendInternal(UUID receiverId, SseEmitter emitter, SseMessage message) {
        try {
            emitter.send(SseEmitter.event()
                    .id(message.id().toString())
                    .name(message.eventName())
                    .data(message.data()));
        } catch (IOException | IllegalStateException e) {
            sseEmitterRepository.delete(receiverId, emitter);
        }
    }
}