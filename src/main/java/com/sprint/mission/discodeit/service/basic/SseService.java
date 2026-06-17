package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.SseMessage;
import com.sprint.mission.discodeit.repository.sse.SseEmitterRepository;
import com.sprint.mission.discodeit.repository.sse.SseMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 1. 클라이언트가 /api/sse로 연결 요청
 2. 서버가 SseEmitter 생성
 3. 사용자 ID 기준으로 emitters Map에 저장
 4. 최초 ping 이벤트 전송
 5. 나중에 send()로 특정 사용자에게 이벤트 전송
 6. broadcast()로 전체 사용자에게 이벤트 전송
 7. 끊긴 연결은 onCompletion, onTimeout, onError, cleanUp으로 제거
 **/

@Service
@RequiredArgsConstructor
public class SseService {
    private final SseEmitterRepository  sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;
    //한시간동안 연결 유지
    private static final Long TIMEOUT = 60 * 60 * 1000L;

    /// SseEmitter 객체 생성.
    /// event, data, id등이 들어있는 객체.
    /// receiverId: 알림 받을 사용자Id
    /// lastEventId: 마지막으로 받은 이벤트 Id -> 재연결 처리용
    public SseEmitter connect(UUID receiverId, UUID lastEventId) {

        /// 클라이언트가 SSE 연결 요청을 하면 sseEmitter 객체를 새로 만든다.
        /// 새 SSE 연결 객체 생성
        /// SseEmitter = 서버에서 클라이언트로 보내는 전용 통로
        SseEmitter sseEmitter = new SseEmitter(TIMEOUT);

        /**
         * receiverId에 해당하는 Set이 있으면 가져오고, 없으면 새 Set을 만들어서 Map에 넣는다.
         * emitters = {"userA" : [] }
         **/
        sseEmitterRepository.save(receiverId, sseEmitter);

        /** 클라이언트가 정상적으로 연결을 종료하면 저장소에서 emitter를 제거합니다. **/
        sseEmitter.onCompletion(() ->
                sseEmitterRepository.delete(receiverId, sseEmitter)
        );

        /** 1시간 동안 연결 유지되다가 timeout이 발생하면 제거합니다. **/
        sseEmitter.onTimeout(() ->
                sseEmitterRepository.delete(receiverId, sseEmitter)
        );

        /** 이벤트 전송 중 에러가 나도 제거합니다. **/
        sseEmitter.onError(error ->
                sseEmitterRepository.delete(receiverId, sseEmitter)
        );

        /// lastEventId가 있으면, 클라이언트가 마지막으로 받은 이벤트 이후의 메시지를 다시 보내준다.
        /// ex) 클라이언트가 A까지 받고 끊겼다면, 서버는 A 이후에 저장된 B, C, D를 재전송한다.
        if (lastEventId != null) {
            /// lastEventId기준 이후 event들을 조회해서 send()
            sseMessageRepository.findAllAfter(receiverId, lastEventId)
                    .forEach(message -> {
                        try {
                            sseEmitter.send(SseEmitter.event()
                                    .id(message.id().toString())
                                    .name(message.eventName())
                                    .data(message.data()));
                        } catch (IOException | IllegalStateException e) {
                            sseEmitterRepository.delete(receiverId, sseEmitter);
                        }
                    });
        }

        /// ping 보내서 SSE 연결상태 검증.
        if (!ping(sseEmitter)) {
            sseEmitterRepository.delete(receiverId, sseEmitter);
        }

        return sseEmitter;
    }

    /** 여러사용자에게 이벤트를 보내는 메서드 **/
    public void send(Collection<UUID> receiverIds, String eventName, Object data) {

        receiverIds.forEach(receiverId -> {
            SseMessage message = sseMessageRepository.save(receiverId, eventName, data);

            List<SseEmitter> userEmitters = sseEmitterRepository.findAllByReceiverId(receiverId);

            userEmitters.forEach(sseEmitter -> {
                try {
                    sseEmitter.send(SseEmitter.event()
                            .id(message.id().toString())
                            .name(message.eventName())
                            .data(message.data()));
                } catch (IOException | IllegalStateException e) {
                    sseEmitterRepository.delete(receiverId, sseEmitter);
                }
            });
        });
    }

    /// 현재 연결된 모든 사용자에게 이벤트 보낸다.
    /// ex) broadcast("notice", "서버 점검이 예정되어 있습니다.")
    // {"userA" : [emittter1, emitter2], "userB" : [emitter3]} 이면 emitter1, emitter2, emitter3 전체에게 전송.
    public void broadcast(String eventName, Object data) {
        send(sseEmitterRepository.findAllReceiverIds(), eventName, data);
    }

    /// 모든 emitter에게 ping을 보내서 ping 성공시 유지, ping 실패시 제거
    @Scheduled(fixedDelay = 1000 * 60 * 30)
    public void cleanUp() {
        sseEmitterRepository.findAllReceiverIds()
                .forEach(receiverId ->
                        sseEmitterRepository.findAllByReceiverId(receiverId)
                                .forEach(sseEmitter -> {
                                    if (!ping(sseEmitter)) {
                                        sseEmitterRepository.delete(receiverId, sseEmitter);
                                    }
                                })
                );
    }

    /// 연결확인용 이벤트를 보내는 메서드
    /// 서버 -> 클라이언트(단방향)
    /// 이미 열려있는 HTTP 연결에 서버가 데이터를 계서 써서 보낸다.
    /// SSE ping은 클라이언트 응답을 받아서 성공판단하는게 아니라, 서버가 쓰기(write)에 성공했는지로 판단.
    private boolean ping(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(UUID.randomUUID().toString())
                    .name("ping")
                    .data("ping"));

            return true;
        } catch (IOException | IllegalStateException e) {
            return false;
        }
    }
}
