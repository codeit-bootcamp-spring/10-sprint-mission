package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.SseMessage;
import com.sprint.mission.discodeit.repository.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
import com.sprint.mission.discodeit.service.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicSseService implements SseService {

    private static final Long DEFAULT_TIMEOUT = 1000L * 60 * 30; // 30분
    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    @Override
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {

        // 새 Emitter 생성
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitter.onTimeout(() -> sseEmitterRepository.remove(receiverId, emitter));
        emitter.onError((e) -> sseEmitterRepository.remove(receiverId, emitter));
        emitter.onCompletion(() -> sseEmitterRepository.remove(receiverId, emitter));

        // 저장소 등록
        sseEmitterRepository.add(receiverId, emitter);

        // 초기 연결 확인용 ping
        ping(emitter);

        // 유실 복원 처리
        if (lastEventId != null) {
            List<SseMessage> missedMessages = sseMessageRepository.findAllByReceiverIdAndAfterId(receiverId, lastEventId);
            missedMessages.forEach(msg -> sendToClient(emitter, msg));
        }

        log.info("[SSE] 신규 연결 등록: UserId={}", receiverId);
        return emitter;
    }

    @Override
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        // DB에 메세지 저장
        SseMessage message = sseMessageRepository.save(receiverIds, eventName, data);

        // 대상자들에게 순차 전송
        for (UUID receiverId : receiverIds) {
            sseEmitterRepository.findAllByUserId(receiverId)
                    .forEach(emitter -> sendToClient(emitter, message));
        }
    }

    @Override
    public void broadcast(String eventName, Object data) {
        // DB에 메세지 저장
        SseMessage message = sseMessageRepository.save(List.of(), eventName, data);

        // 전체 에미터에게 전송
        sseEmitterRepository.findAllEmitters()
                .forEach(emitter -> sendToClient(emitter, message));
    }

    @Scheduled(fixedDelay = 1000 * 45) // 45초마다 점검
    @Override
    public void cleanUp() {
        log.info("[SSE] 연결 상태 점검 시작...");

        sseEmitterRepository.findAll().entrySet().removeIf(entry -> {
            List<SseEmitter> emitters = entry.getValue();

            // 해당 유저의 에미터 중 연결이 끊긴 것들 제거
            emitters.removeIf(emitter -> !ping(emitter));

            // 만약 에미터가 하나도 남지 않았다면 해당 유저(Key) 자체를 맵에서 삭제
            return emitters.isEmpty();
        });

        log.info("[SSE] 연결 상태 점검 완료.");
    }

    private boolean ping(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event()
                    .name("ping")
                    .data("keep-alive"));
            return true;
        } catch (IOException e) {
            emitter.complete();
            return false;
        }
    }

    private void sendToClient(SseEmitter emitter, SseMessage message) {
        try {
            emitter.send(SseEmitter.event()
                    .id(message.id().toString())
                    .name(message.eventName())
                    .data(message.data()));
            log.debug("[SSE] 전송 성공: MessageId={}", message.id());
        } catch (IOException e) {
            log.warn("[SSE] 전송 실패 (연결 끊김): MessageId={}", message.id());
            emitter.complete(); // 실패 시 명시적으로 종료 처리하여 자원 해제
        }
    }
}
