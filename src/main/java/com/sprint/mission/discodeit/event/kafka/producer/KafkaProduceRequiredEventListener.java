package com.sprint.mission.discodeit.event.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.BinaryContentCreatedEvent;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaProduceRequiredEventListener {

    // 메시지를 전송하기 위한 템플릿
    private final KafkaTemplate<String, String> kafkaTemplate;
    // 객체를 json으로 변환해줄 변환기
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("ioTaskExecutor")
    public void handleMessageCreate(MessageCreatedEvent event){
        try{
            // 이벤트 객체를 json문자열로 변환
            String payload = objectMapper.writeValueAsString(event);

            // 토픽 설정 후 메시지를 보냄
            kafkaTemplate.send("discodeit.MessageCreatedEvent", payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null){
                            log.info("[Kafka 발송 성공] 토픽: {}, 파티션: {}, 오프셋: {}",
                                    "discodeit.MessageCreatedEvent",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }else {
                            log.error("[Kafka 발송 실패] 이벤트: {}", payload, ex);
                        }
            });
        } catch (JsonProcessingException e){
            log.error("이벤트 객체 JSON 변환 실패 (발송 취소)",e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("ioTaskExecutor")
    public void handleRoleUpdate(RoleUpdatedEvent event){
        try{
            // 이벤트 객체를 json문자열로 변환
            String payload = objectMapper.writeValueAsString(event);

            // 토픽 설정 후 메시지를 보냄
            kafkaTemplate.send("discodeit.RoleUpdatedEvent", payload)
                    .whenComplete((result, ex) -> {
                        if (ex == null){
                            log.info("[Kafka 발송 성공] 토픽: {}, 파티션: {}, 오프셋: {}",
                                    "discodeit.RoleUpdatedEvent",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }else {
                            log.error("[Kafka 발송 실패] 이벤트: {}", payload, ex);
                        }
                    });
        } catch (JsonProcessingException e){
            log.error("이벤트 객체 JSON 변환 실패 (발송 취소)",e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("ioTaskExecutor")
    public void handleProfileUpload(BinaryContentCreatedEvent event){
        try{
            // 이벤트 객체를 json문자열로 변환
            String payload = objectMapper.writeValueAsString(event);
            String requestId = MDC.get("requestId");

            Message<String> message = MessageBuilder
                    .withPayload(payload)
                    .setHeader(KafkaHeaders.TOPIC, "discodeit.BinaryContentCreatedEvent")
                    .setHeader("requestId", requestId != null ? requestId : "UNKNOWN-ID")
                    .build();

            // 토픽 설정 후 메시지를 보냄
            kafkaTemplate.send(message)
                    .whenComplete((result, ex) -> {
                        if (ex == null){
                            log.info("[Kafka 발송 성공] 토픽: {}, 파티션: {}, 오프셋: {}",
                                    "discodeit.BinaryContentCreatedEvent",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset());
                        }else {
                            log.error("[Kafka 발송 실패] 이벤트: {}", payload, ex);
                        }
                    });
        } catch (JsonProcessingException e){
            log.error("이벤트 객체 JSON 변환 실패 (발송 취소)",e);
        }
    }
}
