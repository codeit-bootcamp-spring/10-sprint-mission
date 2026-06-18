package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RequiredArgsConstructor
@Service
public class SseService {

    private static final long DEFAULT_TIMEOUT = 1000L * 60 * 60;
    private static final String PING_EVENT_NAME = "ping";

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        sseEmitterRepository.save(receiverId, emitter);

        emitter.onCompletion(() -> sseEmitterRepository.delete(receiverId, emitter));
        emitter.onTimeout(() -> sseEmitterRepository.delete(receiverId, emitter));
        emitter.onError(e -> sseEmitterRepository.delete(receiverId, emitter));

        if (!ping(emitter)) {
            sseEmitterRepository.delete(receiverId, emitter);
            return emitter;
        }

        if (lastEventId != null) {
            replay(receiverId, lastEventId, emitter);
        }

        log.debug("SSE 연결: receiverId={}, lastEventId={}", receiverId, lastEventId);
        return emitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        if (receiverIds.isEmpty()) {
            return;
        }
        Set<UUID> receiverSet = Set.copyOf(receiverIds);
        SseMessage message = sseMessageRepository.save(SseMessage.of(receiverSet, eventName, data));

        for (UUID receiverId : receiverSet) {
            List<SseEmitter> emitters = sseEmitterRepository.findAllByReceiverId(receiverId);
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(message.id().toString())
                            .name(eventName)
                            .data(data));
                } catch (IOException e) {
                    log.debug("SSE 전송 실패: receiverId={}, eventName={}", receiverId, eventName);
                    sseEmitterRepository.delete(receiverId, emitter);
                }
            }
        }
    }

    public void broadcast(String eventName, Object data) {
        send(sseEmitterRepository.findAllReceiverIds(), eventName, data);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        log.debug("SSE 정리 시작");
        for (UUID receiverId : sseEmitterRepository.findAllReceiverIds()) {
            for (SseEmitter emitter : sseEmitterRepository.findAllByReceiverId(receiverId)) {
                if (!ping(emitter)) {
                    sseEmitterRepository.delete(receiverId, emitter);
                }
            }
        }
        log.debug("SSE 정리 완료");
    }

    private void replay(UUID receiverId, UUID lastEventId, SseEmitter emitter) {
        List<SseMessage> missed = sseMessageRepository.findAllByReceiverIdAndIdAfter(receiverId, lastEventId);
        for (SseMessage message : missed) {
            try {
                emitter.send(SseEmitter.event()
                        .id(message.id().toString())
                        .name(message.eventName())
                        .data(message.data()));
            } catch (IOException e) {
                log.debug("SSE 재전송 실패: receiverId={}, eventId={}", receiverId, message.id());
                sseEmitterRepository.delete(receiverId, emitter);
                return;
            }
        }
    }

    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event().name(PING_EVENT_NAME).data(""));
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
