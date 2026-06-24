package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import com.sprint.mission.discodeit.sse.SseMessage;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

    private static final long TIMEOUT = 60L * 60 * 1000;

    private final SseEmitterRepository emitterRepository;
    private final SseMessageRepository messageRepository;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(TIMEOUT);
        emitterRepository.save(receiverId, emitter);
        emitter.onCompletion(() -> emitterRepository.delete(receiverId, emitter));
        emitter.onTimeout(() -> emitterRepository.delete(receiverId, emitter));
        ping(emitter);
        if (lastEventId != null) {
            messageRepository.findMissedEvents(receiverId, lastEventId)
                    .forEach(m -> sendTo(emitter, m));
        }
        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = SseMessage.of(eventName, data, receiverIds);
        messageRepository.save(message);
        receiverIds.forEach(receiverId -> emitterRepository.findByReceiverId(receiverId)
                .forEach(emitter -> sendTo(emitter, message)));
    }

    @Override
    public void broadcast(String eventName, Object data) {
        SseMessage message = SseMessage.of(eventName, data, null);
        messageRepository.save(message);
        emitterRepository.findAll().values()
                .forEach(list -> list.forEach(emitter -> sendTo(emitter, message)));
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        emitterRepository.findAll().forEach((receiverId, list) ->
                list.forEach(emitter -> {
                    if (!ping(emitter)) {
                        emitterRepository.delete(receiverId, emitter);
                    }
                }));
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().comment("ping"));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void sendTo(SseEmitter emitter, SseMessage message) {
        try {
            emitter.send(SseEmitter.event()
                    .id(message.id().toString())
                    .name(message.eventName())
                    .data(message.data()));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }
    }
}
