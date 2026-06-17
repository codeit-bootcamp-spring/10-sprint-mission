package com.sprint.mission.discodeit.event.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.event.MessageCreatedEvent;
import com.sprint.mission.discodeit.event.RoleUpdatedEvent;
import com.sprint.mission.discodeit.event.S3UploadFailedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/*
애플리케이션 내부에서 발행된 스프링 이벤트를 받아 Kafka 토픽으로 다시 발행(produce)하는 리스너

# 구조
도메인 로직
-> ApplicationEventPublisher로 스프링 이벤트 발행
-> 이 리스너가 수신
-> Kafka 토픽에 메시지 전송

- 도메인 서비스는 이벤트만 발행 / Kafka 전송 책임은 이 클래스에서
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProduceRequiredEventListener {

  public static final String TOPIC_MESSAGE_CREATED = "discodeit.MessageCreatedEvent";
  public static final String TOPIC_ROLE_UPDATED = "discodeit.RoleUpdatedEvent";
  public static final String TOPIC_S3_UPLOAD_FAILED = "discodeit.S3UploadFailedEvent";

  // KafkaTemplate - Kafka에 메시지를 보내는 기능을 제공하는 Spring 클래스
  // key/value 모두 String으로 직렬화 -> application.yaml 설정과 동일해야함
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  // 메시지 생성 이벤트를 Kafka로 발행
  @Async("eventTaskExecutor") // 비동기 실행 - Kafka 전송이 느려도 메시지 저장 스레드를 블로킹 하지 않음
  @TransactionalEventListener
  public void on(MessageCreatedEvent event) {
    send(TOPIC_MESSAGE_CREATED, event);
  }

  @Async("eventTaskExecutor")
  @TransactionalEventListener
  public void on(RoleUpdatedEvent event) {
    send(TOPIC_ROLE_UPDATED, event);
  }

  // S3 실패는 JPA 트랜잭션 밖에서 발행됨 -> 일반 @EventListener (AFTER_COMMIT 을 기다릴 필요가 없으므로)
  @Async("eventTaskExecutor")
  @EventListener
  public void on(S3UploadFailedEvent event) {
    Throwable e = event.getE();
    String reason = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
    // Kafka 전송에 필요한 핵심 정보만 담은 별도 페이로드 객체로 변환해서 전송
    send(TOPIC_S3_UPLOAD_FAILED,
        new S3UploadFailedKafkaPayload(event.getBinaryContentId(), event.getRequestId(), reason));
  }

  // 페이로드 객체를 JSON으로 직렬화해 지정한 토픽으로 전송하는 공통 메서드
  private void send(String topic, Object payload) {
    try {
      // 객체를 JSON 문자열로 직렬화한 뒤 Kafka 토픽으로 비동기 전송
      kafkaTemplate.send(topic, objectMapper.writeValueAsString(payload));
    } catch (JsonProcessingException ex) {
      // 직렬화 단계에서 실패한 경우(잘못된 객체 구조 등) 예외를 삼키고 에러 로그만 남김
      // 알림 발행은 부가 기능이므로, 직렬화 실패가 원래 비즈니스 로직을 중단시키지 않도록 함
      log.error("Kafka 직렬화 실패 topic={}", topic, ex);
    }
  }

}
