package com.sprint.mission.discodeit.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Slf4j
@Configuration
public class KafkaConfig {

  /**
   * 분산 환경에서 WebSocket/SSE 브로드캐스트를 위한 컨슈머 팩토리.
   * 각 인스턴스가 모든 이벤트를 수신해야 하므로 인스턴스마다 고유한 group ID를 사용한다.
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> broadcastKafkaListenerContainerFactory(
      ConsumerFactory<String, String> consumerFactory,
      @Value("${spring.application.name}") String appName) {

    Map<String, Object> props = new HashMap<>(consumerFactory.getConfigurationProperties());
    String groupId = appName + "-broadcast-" + UUID.randomUUID();
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    log.info("Kafka 브로드캐스트 컨슈머 그룹 ID: {}", groupId);

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
    return factory;
  }
}
