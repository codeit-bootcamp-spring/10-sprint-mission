package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.sse.SseMessage;
import com.sprint.mission.discodeit.repository.EmitterRepository;
import com.sprint.mission.discodeit.repository.SseMessageRepository;
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
public class SseService {
    private final EmitterRepository emitterRepository;
    private final SseMessageRepository sseMessageRepository;

    private final static long TIMEOUT = 60L * 60L * 1000L;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);
        sseEmitter.onCompletion(() -> {
            log.info("SSE 연결 완료. receiverId = {}", receiverId);
            emitterRepository.remove(receiverId, sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            log.info("SSE 연결 타임아웃. receiverId = {}", receiverId);
            emitterRepository.remove(receiverId, sseEmitter);
        });
        sseEmitter.onError(e -> {
            log.info("SSE 연결 에러. receiverId = {}", receiverId);
            emitterRepository.remove(receiverId, sseEmitter);
        });

        emitterRepository.add(receiverId, sseEmitter);

        if(lastEventId != null){
            List<SseMessage> lastMessages = sseMessageRepository.findAllAfter(lastEventId);
            for(SseMessage message : lastMessages){
                if(message.getReceiverId().equals(receiverId)){
                    try {
                        sseEmitter.send(
                                SseEmitter.event()
                                        .id(message.getEventId().toString())
                                        .name(message.getEventName())
                                        .data(message.getData())
                        );
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }
            }
        }
        log.info("SSE emitter 등록 완료. receiverId = {}, lastEventId = {}", receiverId, lastEventId);

        // 연결 직후 메시지를 보내 통로를 열어둠
        boolean isAlive = ping(sseEmitter);
        if(!isAlive){
            emitterRepository.remove(receiverId, sseEmitter);
        }
        return sseEmitter;
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {

        for(UUID receiverId : receiverIds){
            List<SseEmitter> emitters = emitterRepository.findAllByReceiverId(receiverId);
            SseMessage message = new SseMessage(receiverId,UUID.randomUUID(), eventName, data);
            sseMessageRepository.save(message);

            for(SseEmitter emitter : emitters){
                try{
                    emitter.send(
                            SseEmitter.event()
                                    .id(message.getEventId().toString())
                                    .name(eventName)
                                    .data(data)
                    );
                } catch (Exception e){
                    log.info("sseEmiter연결이 끊겼습니다. receiverId = {}", receiverId);
                    emitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    public void broadcast(String eventName, Object data) {
        List<UUID> receivers =  emitterRepository.findAllReceiverIds();
        send(receivers, eventName, data);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        log.info("[SSE clean up 스케줄러 시작]");
        List<UUID> receiverIds = emitterRepository.findAllReceiverIds();
        for(UUID receiverId : receiverIds){
            List<SseEmitter> emitters = emitterRepository.findAllByReceiverId(receiverId);
            for(SseEmitter emitter : emitters){
                boolean isAlive = ping(emitter);
                if(!isAlive){
                    log.info("만료된 emitter 발견. 유저 ID: {}", receiverId);
                    emitterRepository.remove(receiverId, emitter);
                }
            }
        }
    }

    private boolean ping(SseEmitter sseEmitter) {
        try{
            sseEmitter.send(SseEmitter.event()
                    .name("ping")
                    .data("핑"));
            return true;
        } catch (Exception e){
            return false;
        }
    }
}